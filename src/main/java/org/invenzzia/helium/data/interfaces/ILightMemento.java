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
package org.invenzzia.helium.data.interfaces;

/**
 * Light memento can be used everywhere, where we do not need the access
 * to the full domain model.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ILightMemento {
	/**
	 * The method shall return a memento which persists some internal object state.
	 * 
	 * @return 
	 */
	public Object getMemento();
	/**
	 * The method shall restore the state from the memento. {@link StateReverter}
	 * guarantees that the passed memento will have the proper class.
	 * 
	 * @param memento Memento to restore.
	 */
	public void restoreMemento(Object memento);
}
