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

import java.lang.ref.WeakReference;

/**
 * Instances of this class carry information about the content of the history. It
 * can be used to display the history browser in the GUI. The records hold only
 * a weak reference to the command they represent to avoid accidental memory
 * leaks. In order to find a matching command, we must compare the record against
 * all the commands with {@link #acceptsCommand(T cmd)}.
 * 
 * @param T command interface.
 * @author Tomasz Jędrzejewski
 */
public class CommandInfo<T> {
	/**
	 * The name of the command in the history.
	 */
	private final String name;
	/**
	 * Is this a future operation?
	 */
	private boolean future;
	/**
	 * Is this a base command representing the initial state?
	 */
	private final boolean base;
	/**
	 * Command type.
	 */
	private final Class<? extends T> type;
	/**
	 * Weak reference to the original command, to avoid accidental memory leak.
	 */
	private final WeakReference<T> ticket;
	
	/**
	 * Creates a new record describing a single command in the history.
	 * 
	 * @param name Command name.
	 * @param type Command type.
	 * @param ticket Command instance.
	 */
	public CommandInfo(String name, Class<? extends T> type, T ticket, boolean base) {
		this.name = name;
		this.type = type;
		this.ticket = new WeakReference(ticket);
		this.base = base;
	}
	
	/**
	 * Returns the name of the represented command.
	 * 
	 * @return Command name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns true, if this is a base command representing the initial state
	 * that cannot be undone, and redone.
	 * 
	 * @return True, if this is a base command.
	 */
	public boolean isBase() {
		return this.base;
	}
	
	/**
	 * Returns true, if this is a future operation.
	 * 
	 * @return 
	 */
	public boolean isFuture() {
		return this.future;
	}
	
	/**
	 * Sets the new future state.
	 * 
	 * @param future New state.
	 */
	void setFuture(boolean future) {
		this.future = future;
	}
	
	/**
	 * Returns the type of the represented command.
	 * 
	 * @return Command type.
	 */
	public Class<? extends T> getType() {
		return this.type;
	}
	
	/**
	 * Returns true, if the specified command is the one represented by this
	 * record.
	 * 
	 * @param cmd The command to check.
	 * @return True, if the record represents this command.
	 */
	public boolean acceptsCommand(T cmd) {
		T keptCommand = this.ticket.get();
		return keptCommand == cmd;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
