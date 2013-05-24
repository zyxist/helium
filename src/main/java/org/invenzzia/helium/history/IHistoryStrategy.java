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

/**
 * History strategy decides, how to perform 'execute', 'undo' and 'redo' operations
 * on history records. History manager delegates the concrete tasks to the
 * implementation of this interface.
 * 
 * @param T Interface that defines a command.
 * @author Tomasz Jędrzejewski
 */
public interface IHistoryStrategy<T> {
	/**
	 * Executes the given command.
	 * 
	 * @param command The command.
	 * @throws Exception Exceptions from the command executor, if any.
	 */
	public void execute(T command) throws Exception;
	/**
	 * Undoes the given command. The undo should not fail, if the execution
	 * has been successful.
	 * 
	 * @param command The command to undo.
	 */
	public void undo(T command);
	/**
	 * Replays the previously undone command. This is kept separately from the
	 * original execution, because if the state has some unique ID-s, their restoration
	 * may look differently than generating them during the original execution.
	 * 
	 * <p>Similarly to {@link #undo}, redo shall not fail.
	 * 
	 * @param command The command to redo.
	 */
	public void redo(T command);
	/**
	 * Constructs a fake command that represents the initial state of the history.
	 * This command cannot be undone and redone. Execute method is not performed
	 * for it, too.
	 * 
	 * @return Fake base command.
	 */
	public T getBaseCommand();
}
