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

import org.invenzzia.helium.exception.ModelException;

/**
 * Represents an in-memory data record manager that supports inserting, updating,
 * removing and finding records.
 * 
 * @param R The type of records stored in the data manager.
 * @author Tomasz Jędrzejewski
 */
public interface ICRUDManager<R extends IIdentifiable> {
	/**
	 * Adds a new item to the manager. If the item cannot be added, an exception
	 * should be thrown.
	 * 
	 * @param item The new item to add.
	 * @throws ModelException 
	 */
	public void addItem(R item) throws ModelException;
	/**
	 * Updates the existing item. If the item cannot be updated, an exception should
	 * be thrown.
	 * 
	 * @param item The item to update.
	 * @throws ModelException 
	 */
	public void updateItem(R item) throws ModelException;
	/**
	 * Removes an item with the specified ID. If the item cannot be removed or it does
	 * not exist, the method should throw an exception.
	 * 
	 * @param id Record ID
	 * @throws ModelException 
	 */
	public void removeItem(long id) throws ModelException;
	/**
	 * Removes an existing item. If the item cannot be removed, an exception should
	 * be thrown.
	 * 
	 * @param item
	 * @throws ModelException 
	 */
	public void removeItem(R item) throws ModelException;
	/**
	 * Finds a record by its ID. If the record does not exist, <strong>null</strong> shall
	 * be returned.
	 * 
	 * @param id Record ID.
	 * @return Record object or null.
	 */
	public R findById(long id);
}
