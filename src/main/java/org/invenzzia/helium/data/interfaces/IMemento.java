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
 * Represents a data model object that can emit its memento representation
 * that allows to undo the operation.
 * 
 * @param D domain model
 * @author Tomasz Jędrzejewski
 */
public interface IMemento<D> {
	/**
	 * Creates a memento with the persisted current state.
	 * 
	 * @param domainModel Sometimes this method might need an access to the domain model.
	 * @return Memento of the current state. 
	 */
	public Object getMemento(D domainModel);
	/**
	 * Restores the object state from the memento.
	 * 
	 * @throws IllegalArgumentException If the memento is invalid.
	 * @param object The memento to restore.
	 * @param domainModel Sometimes this method might need an access to the domain model.
	 */
	public void restoreMemento(Object object, D domainModel);
}
