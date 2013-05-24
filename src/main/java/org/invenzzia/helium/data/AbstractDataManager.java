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
package org.invenzzia.helium.data;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.invenzzia.helium.data.interfaces.ICRUDManager;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.exception.ModelException;

/**
 * The data manager is used for a custom semi-persistence solutions for in-memory
 * object graphs creating a data model.
 * 
 * @param R The type of records stored in the data manager.
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractDataManager<R extends IIdentifiable> implements ICRUDManager<R> {
	/**
	 * Auto-incrementation.
	 */
	private long nextId = IIdentifiable.INCREMENTATION_START;
	/**
	 * Stores all the records.
	 */
	private Map<Long, R> records;
	
	public AbstractDataManager() {
		this.records = new LinkedHashMap<>();
	}
	
	/**
	 * Sets the next ID for automatic generation. The method shall be used only when reading
	 * the state from the disk.
	 * 
	 * @param nextId 
	 */
	public final void setNextId(long nextId) {
		this.nextId = nextId;
	}
	
	/**
	 * Retrieves the current value of the next ID. The method shall be used only when
	 * writing the state to the disk.
	 * 
	 * @return Next ID value.
	 */
	public final long getNextId() {
		return this.nextId;
	}
	
	/**
	 * Adds a new item to the manager.
	 * 
	 * @param mot 
	 */
	@Override
	public final void addItem(R mot) throws ModelException {
		if(mot.getId() != IIdentifiable.NEUTRAL_ID) {
			throw new IllegalArgumentException("Cannot add a previously added record.");
		}
		try {
			mot.setId(this.nextId++);

			this.beforeCreate(mot);
			this.records.put(mot.getId(), mot);
			this.afterCreate(mot);
		} catch(ModelException exception) {
			this.nextId--;
			throw exception;
		}
	}
	
	/**
	 * Updates the internal data structures, when the record is changed.
	 * 
	 * @throws ModelException Problems with the data integrity.
	 * @param mot Changed record.
	 */
	@Override
	public void updateItem(R mot) throws ModelException {
	}
	
	/**
	 * Removes the mean of transport with the specified ID.
	 * 
	 * @param id 
	 */
	@Override
	public final void removeItem(long id) throws ModelException {
		R item = this.records.get(id);
		if(null == item) {
			throw new ModelException("The record with ID #"+id+" does not exist.");
		}
		this.beforeRemove(item);
		this.records.remove(id);
		this.afterRemove(item);
	}
	
	/**
	 * Removes the specified mean of transport.
	 * 
	 * @param mot 
	 */
	@Override
	public final void removeItem(R mot) throws ModelException {
		R stored = this.records.get(mot.getId());
		if(null == stored || stored != mot) {
			throw new ModelException("The record with ID #"+mot.getId()+" does not exist.");
		}
		this.beforeRemove(mot);
		this.records.remove(mot.getId());
		this.afterRemove(mot);
	}
	
	/**
	 * Returns all the means of transport.
	 * 
	 * @return Immutable list of means of transport.
	 */
	public final List<R> getRecords() {
		return ImmutableList.copyOf(this.records.values());
	}
	
	/**
	 * Returns the number of currently stored records.
	 * 
	 * @return Number of records.
	 */
	public final int size() {
		return this.records.size();
	}
	
	/**
	 * Filters the means of transport that match the specified condition.
	 * 
	 * @param predicate
	 * @return 
	 */
	public final List<R> filter(Predicate<R> predicate) {
		List<R> results = new LinkedList<>();
		for(R mot: this.records.values()) {
			if(predicate.apply(mot)) {
				results.add(mot);
			}
		}
		return results;
	}
	
	/**
	 * Finds a single record with the given ID.
	 * 
	 * @param id Internal ID.
	 * @return Data record or NULL.
	 */
	@Override
	public final R findById(long id) {
		return this.records.get(id);
	}
	
	/**
	 * Custom code for record creation can be put here. The method can terminate the
	 * addition by throwing an exception.
	 * 
	 * @param item 
	 */
	protected void beforeCreate(R item) throws ModelException {
	}
	
	/**
	 * Custom code for record creation can be put here. The method shall not fail.
	 * 
	 * @param item 
	 */
	protected void afterCreate(R item) {
	}
	
	/**
	 * Custom code for record removal can be put here. The method can terminate the
	 * addition by throwing an exception.
	 * 
	 * @param item 
	 */
	protected void beforeRemove(R item) throws ModelException {
	}
	
	/**
	 * Custom code for record removal can be put here. The method shall not fail.
	 * 
	 * @param item 
	 */
	protected void afterRemove(R item) {
	}
	
	/**
	 * Raw object adding, i.e. for implementing mementos etc.
	 * 
	 * @param id Id of the object to add.
	 * @param item The object to add.
	 */
	protected void addObject(long id, R item) {
		Long idVal = Long.valueOf(id);
		if(this.records.containsKey(idVal)) {
			throw new IllegalArgumentException("The object with the same ID #"+idVal+" is added twice. Check your memento/record implementation whether you have copied the ID.");
		}
		this.records.put(idVal, Preconditions.checkNotNull(item));
	}
}
