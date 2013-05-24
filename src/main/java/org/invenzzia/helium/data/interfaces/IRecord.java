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
 * Record is a copy of the original data object that can be used by GUI etc.
 * and manipulated without altering the original object state. Internally,
 * records can be also used as mementos.
 * 
 * @param T The backed original object type.
 * @param D Domain model
 * @author Tomasz Jędrzejewski
 */
public interface IRecord<T, D> {
	/**
	 * Exports the state from the record to the original object.
	 * 
	 * @param original 
	 * @param domainModel Sometimes this operation might need an access to the domain model.
	 */
	public void exportData(T original, D domainModel);
	/**
	 * Imports the state from the original state to the record.
	 * 
	 * @param original 
	 * @param domainModel Sometimes this operation might need an access to the domain model.
	 */
	public void importData(T original, D domainModel);
}
