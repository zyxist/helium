/*
 * Helium - a set of useful stuff for java.
 * 
 * Helium is free software: you can redistribute it and/or modify
 * it under the terms of the New BSD license as published by
 * Invenzzia Group.
 *
 * Helium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the new BSD license
 * along with Helium. If not, see <http://invenzzia.org/license/new-bsd>.
 */
package org.invenzzia.helium.history;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.events.HistoryCommandExecutedEvent;
import org.invenzzia.helium.events.HistoryCommandReplayedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;

/**
 * Represents a history of changes, offering the 'undo' and 'redo' operations.
 * The implementation keeps two buffers of 'future' and 'past' operations, and tracks
 * their current capacity to discard the oldest records.
 * 
 * <p>It is assumed that the forward ("execution") operation may fail, but if it 
 * succeeded, "undo" must also succeed. It is left to the programmer to guarantee
 * this assumption.
 * 
 * <p>The user of the class is required to provide the name of the interface that
 * represents commands (through the generic parameter) and a strategy that defines,
 * what 'execute' and 'undo' actually mean for these commands.
 * 
 * @param T Command interface
 * @author Tomasz Jędrzejewski
 */
public class History<T> {
	/**
	 * Past operations that we can undo. The newest operation has the highest index.
	 */
	private List<T> pastOperations;
	/**
	 * Future operations that we can redo. The operation most close to the current one
	 * has the index 0 and the most distant is at the highest index.
	 */
	private List<T> futureOperations;
	/**
	 * Base command used for representing the initial state. It cannot be undone, redone,
	 * etc.
	 */
	private T baseCommand;
	/**
	 * Meta-information that can be used by GUi.
	 */
	private Map<T, CommandInfo> commandInformation;
	/**
	 * Delegate that performs the actual undo/redo operations on commands.
	 */
	private final IHistoryStrategy<T> historyStrategy;
	/**
	 * Where to send notifications about the state change?
	 */
	private final EventBus eventBus;
	/**
	 * The history capacity. If it is reached, old commands are discarded.
	 */
	private int maximumCapacity;
	
	/**
	 * Initializes the history manager.
	 */
	public History(IHistoryStrategy<T> historyStrategy, EventBus eventBus) {
		this.historyStrategy = Preconditions.checkNotNull(historyStrategy, "The history strategy for the history manager cannot be empty.");
		this.eventBus = Preconditions.checkNotNull(eventBus, "The history manager needs an event bus.");
		
		this.pastOperations = new LinkedList<>();
		this.futureOperations = new LinkedList<>();
		this.commandInformation = new LinkedHashMap<>();
		this.baseCommand = this.historyStrategy.getBaseCommand();
		this.commandInformation.put(this.baseCommand, this.buildCommandInfo(baseCommand, true));
	}
	
	/**
	 * Returns the value of the maximum history capacity.
	 * 
	 * @return Maximum history capacity.
	 */
	public int getMaximumCapacity() {
		return this.maximumCapacity;
	}
	
	/**
	 * Sets the new history capacity.
	 * 
	 * @param newCapacity New history capacity.
	 */
	public void setMaximumCapacity(int newCapacity) {
		Preconditions.checkArgument(newCapacity > 0, "The maximum history capacity must be greater than 0.");
		this.maximumCapacity = newCapacity;
	}
	
	/**
	 * Returns the number of past operations. The value does not include
	 * the base command representing the initial state.
	 * 
	 * @return 
	 */
	public int getPastOperationNum() {
		return this.pastOperations.size();
	}
	
	/**
	 * Returns the number of future operations.
	 * 
	 * @return 
	 */
	public int getFutureOperationNum() {
		return this.futureOperations.size();
	}
	
	/**
	 * Returns the base command.
	 * 
	 * @return Base command. 
	 */
	public T getBaseCommand() {
		return this.baseCommand;
	}
	
	/**
	 * Removes all entries from the history.
	 */
	public void clear() {
		this.doClear();
		this.eventBus.post(new HistoryChangedEvent<>(this));
	}
	
	/**
	 * The actual clearing code that does not emit events.
	 */
	private void doClear() {
		CommandInfo cmdInfo = this.commandInformation.get(this.baseCommand);
		this.pastOperations.clear();
		this.futureOperations.clear();
		this.commandInformation.clear();
		this.commandInformation.put(this.baseCommand, cmdInfo);
	}
	
	/**
	 * Discards all the past operations that exceed the capacity limit.
	 */
	public void discard() {
		this.doDiscard();
		this.eventBus.post(new HistoryChangedEvent<>(this));
	}
	
	/**
	 * Actual implementation of discard that does not send any events.
	 */
	private void doDiscard() {
		int currentCapacity = this.pastOperations.size() + this.futureOperations.size();
		while(currentCapacity > this.maximumCapacity) {
			T removedCommand;
			if(this.pastOperations.isEmpty()) {
				// We remove the most distant future operation in order not to break the continuum.
				removedCommand = this.futureOperations.remove(this.futureOperations.size() - 1);
			} else {
				// We remove the oldest operation in order not to break the continuum.
				removedCommand = this.pastOperations.remove(0);
			}
			this.commandInformation.remove(removedCommand);
			currentCapacity--;
		}
	}
	
	/**
	 * Executes the given command and puts it into the history of past operations.
	 * All the exceptions are wrapped into the {@link CommandExecutionException}. It
	 * is assumed that the execution failure leaves the system in the consistent state.
	 * The method discards the oldest change in the history at the same time.
	 * 
	 * @param command Command to execute.
	 * @throws CommandExecutionException 
	 */
	public void execute(T command) throws CommandExecutionException {
		Preconditions.checkNotNull(command, "Attempt to execute an empty command.");
		try {
			this.historyStrategy.execute(command);
			
			for(T futureCmd: this.futureOperations) {
				this.commandInformation.remove(futureCmd);
			}
			this.futureOperations.clear();

			this.commandInformation.put(command, this.buildCommandInfo(command, false));
			this.pastOperations.add(command);
			this.doDiscard();
			this.eventBus.post(new HistoryCommandExecutedEvent<>(this));
		} catch(Throwable thr) {
			throw new CommandExecutionException(thr);
		}
	}
	
	/**
	 * Undoes the last operation. It is assumed that this operation shall always succeed, but
	 * if it somehow fails, the exception is wrapped into {@link CommandExecutionException},
	 * and the history is invalidated. The successful undone operation is moved to the future
	 * history buffer.
	 * 
	 * @throws CommandExecutionException 
	 */
	public void undo() throws CommandExecutionException {
		if(!this.pastOperations.isEmpty()) {
			T command = this.pastOperations.remove(this.pastOperations.size() - 1);
			try {
				this.historyStrategy.undo(command);
				this.futureOperations.add(0, command);
				
				this.commandInformation.get(command).setFuture(true);
				this.eventBus.post(new HistoryCommandReplayedEvent<>(this));
			} catch(Throwable thr) {
				this.doClear();
				this.eventBus.post(new HistoryChangedEvent<>(this));
				throw new CommandExecutionException(thr);
			}
		}
	}
	
	/**
	 * Redoes the last operation. It is assumed that this operation shall always succeed, but
	 * if it somehow fails, the exception is wrapped into {@link CommandExecutionException},
	 * and the history is invalidated. The successful redo operation is moved to the past
	 * history buffer.
	 * 
	 * @throws CommandExecutionException 
	 */
	public void redo() throws CommandExecutionException {
		if(!this.futureOperations.isEmpty()) {
			T command = this.futureOperations.remove(0);
			try {
				this.historyStrategy.redo(command);
				this.pastOperations.add(command);

				this.commandInformation.get(command).setFuture(false);
				this.eventBus.post(new HistoryCommandReplayedEvent<>(this));
			} catch(Throwable thr) {
				this.doClear();
				this.eventBus.post(new HistoryChangedEvent<>(this));
				throw new CommandExecutionException(thr);
			}
		}
	}
	
	/**
	 * Jumps to a specific moment in the history.
	 * 
	 * @param info Specific moment in the history
	 * @throws CommandExecutionException 
	 */
	public void jumpTo(CommandInfo info) throws CommandExecutionException {
		if(info.isBase()) {
			int size = this.pastOperations.size();
			for(int i = size - 1; i >= 0; i--) {
				T cmd = this.pastOperations.remove(i);
				this.historyStrategy.undo(cmd);
				this.commandInformation.get(cmd).setFuture(true);
				this.futureOperations.add(0, cmd);
			}
		} else if(info.isFuture()) {
			T cmd;
			do {
				cmd = this.futureOperations.remove(0);
				this.historyStrategy.redo(cmd);
				this.commandInformation.get(cmd).setFuture(false);
				this.pastOperations.add(cmd);
			} while(!info.acceptsCommand(cmd));
		} else {
			T cmd = this.pastOperations.get(this.pastOperations.size() - 1);
			while(!info.acceptsCommand(cmd)) {
				cmd = this.pastOperations.remove(this.pastOperations.size() - 1);
				this.historyStrategy.undo(cmd);
				this.commandInformation.get(cmd).setFuture(true);
				this.futureOperations.add(0, cmd);
				cmd = this.pastOperations.get(this.pastOperations.size() - 1);
			}
		}
		this.eventBus.post(new HistoryCommandReplayedEvent<>(this));
	}
	
	/**
	 * Undoes the changes until the given moment in the past history. The method
	 * invokes {@link History#undo} method and its invocation constraints also apply
	 * here.
	 * 
	 * @param info The command information pointing to the last command we want to undo.
	 * @throws CommandExecutionException 
	 */
	public void undoUntil(CommandInfo info) throws CommandExecutionException {
		int size = this.pastOperations.size();
		for(int i = size - 1; i >= 0; i--) {
			T cmd = this.pastOperations.get(i);
			this.undo();
			if(info.acceptsCommand(cmd)) {
				break;
			}
		}
	}
	
	/**
	 * Redoes the changes until the given moment in the future history. The method
	 * invokes {@link History#redo} method and its invocation constraints also apply
	 * here.
	 * 
	 * @param info The command information pointing to the last command we want to redo.
	 * @throws CommandExecutionException 
	 */
	public void redoUntil(CommandInfo<T> info) throws CommandExecutionException {
		int size = this.futureOperations.size();
		for(int i = size - 1; i >= 0; i--) {
			T cmd = this.futureOperations.get(i);
			this.redo();
			if(info.acceptsCommand(cmd)) {
				break;
			}
		}
	}
	
	/**
	 * Returns true, if there are any operations in the past history. The
	 * value does not include the base command representing the initial
	 * state.
	 * 
	 * @return True, if the past history is not empty.
	 */
	public boolean hasPastOperations() {
		return !this.pastOperations.isEmpty();
	}
	
	/**
	 * Returns true, if there are any operations in the future history.
	 * 
	 * @return True, if the future history is not empty.
	 */
	public boolean hasFutureOperations() {
		return !this.futureOperations.isEmpty();
	}
	
	/**
	 * Returns the information about the past history. The returned list
	 * is generated, so it does not depend on the state of {@link History},
	 * however the command information records are preserved between the
	 * calls of this method.
	 * 
	 * <p>The order of elements is as follows:</p>
	 * <ul>
	 *  <li>Base command representing the initial state,</li>
	 *  <li>All past operations from the oldest to the newest</li>
	 *  <li>All future operations from the most close to the most distant.</li>
	 * </ul>
	 * 
	 * <p>The order of elements preserves the chronology.</p>
	 * 
	 * @return History of past operations.
	 */
	public List<CommandInfo<T>> getHistory() {
		List<CommandInfo<T>> info = new ArrayList<>(this.getPastOperationNum() + this.getFutureOperationNum() + 1);
		info.add(this.commandInformation.get(this.baseCommand));
		for(T cmd: this.pastOperations) {
			info.add(this.commandInformation.get(cmd));
		}
		for(T cmd: this.futureOperations) {
			info.add(this.commandInformation.get(cmd));
		}
		return info;
	}
	
	/**
	 * Retrieves information about the given command that can be shown in the GUI.
	 * 
	 * @param command The command to scan.
	 * @param base Is this a base command?
	 * @return Information about the command.
	 */
	private CommandInfo<T> buildCommandInfo(T command, boolean base) {
		String name;
		if(command instanceof ICommandDetails) {
			name = ((ICommandDetails)command).getCommandName();
		} else {
			CommandDetails details = command.getClass().getAnnotation(CommandDetails.class);
			if(null != details) {
				name = details.name();
			} else {
				name = command.getClass().getSimpleName();
			}
		}
		return new CommandInfo<>(name, (Class<? extends T>) command.getClass(), command, base);
	}
}
