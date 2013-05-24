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
 * Managers shall also support restoring the mementos in order to handle
 * removed objects.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IManagerMemento {
	/**
	 * Restores the removed object from its memento.
	 * 
	 * @throws IllegalArgumentException If the memento is invalid.
	 * @param object The memento to restore.
	 */
	public void restoreMemento(Object object);
}
