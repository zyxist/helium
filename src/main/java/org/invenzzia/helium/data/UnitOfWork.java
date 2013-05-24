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

import java.util.*;
import org.invenzzia.helium.data.interfaces.IIdentifiable;

/**
 * Unit of work represents a set of changes on the data model that need to be applied.
 * It divides the changes into three sets: new records, updated records, and removed
 * records. The record can belong to more than one set. For example, we can first
 * update some record to make the removal possible, and then remove it.
 * 
 * @param R managed records
 * @author Tomasz Jędrzejewski
 */
public class UnitOfWork<R extends IIdentifiable> {
	enum RecordStatus {
		NEW, MODIFIED, REMOVED
	};
	/**
	 * Provides an access to all the record modified in this change set. Mementos are
	 * created only while inserting a record to this set. If a record is updated several
	 * times, we remember the memento only during the first change recording.
	 */
	private Set<R> changedRecords;
	/**
	 * Record status allows proper processing while updating the real model state.
	 */
	private Map<R, RecordStatus> recordStatuses;
	/**
	 * Number of inserts performed within this unit of work.
	 */
	private int insertNum = 0;
	/**
	 * Number of updates performed within this unit of work.
	 */
	private int updateNum = 0;
	/**
	 * Number of removes performed within this unit of work.
	 */
	private int removeNum = 0;

	public UnitOfWork() {
		this.changedRecords = new LinkedHashSet<>();
		this.recordStatuses = new LinkedHashMap<>();
	}

	/**
	 * Returns true, if the unit of work does not contain any changes.
	 * 
	 * @return True, if there are no changes.
	 */
	public boolean isEmpty() {
		return this.changedRecords.isEmpty();
	}

	/**
	 * Returns true, if the update or remove sets contain at least one element.
	 * 
	 * @return True, if the unit of work will modify an existing state.
	 */
	public boolean isUpdatingExistingState() {
		return this.updateNum > 0 || this.removeNum > 0;
	}
	
	/**
	 * Returns the number of records to insert.
	 * 
	 * @return 
	 */
	public int getInsertNum() {
		return this.insertNum;
	}
	
	/**
	 * Returns the number of records to update.
	 * @return 
	 */
	public int getUpdateNum() {
		return this.updateNum;
	}
	
	/**
	 * Returns the number of records to remove.
	 * 
	 * @return 
	 */
	public int getRemoveNum() {
		return this.removeNum;
	}

	/**
	 * Specifies a new record to insert. The method can be called several times
	 * on the same object, but it cannot be used for records that have the ID number
	 * assigned.
	 * 
	 * @param record The record to insert.
	 */
	public void insert(R record) {
		if(record.getId() != IIdentifiable.NEUTRAL_ID) {
			throw new IllegalArgumentException("The record '"+record.toString()+"' is already in the data model and cannot be inserted again.");
		}
		if(!this.changedRecords.contains(record)) {
			this.changedRecords.add(record);
			this.recordStatuses.put(record, RecordStatus.NEW);
			this.insertNum++;
		}
	}
	
	/**
	 * Specifies an existing record to update. The method can be called several times
	 * on the same object, and even on inserted objects - it recognizes the proper
	 * state and knows, what to do with that.
	 * 
	 * @param record The record to update.
	 */
	public void update(R record) {
		if(!this.changedRecords.contains(record)) {
			this.changedRecords.add(record);
			this.recordStatuses.put(record, RecordStatus.MODIFIED);
			this.updateNum++;
		}
	}

	/**
	 * Specifies an existing record to remove. The method can be called several times
	 * on the same object, but it cannot be used for records that do not have the ID
	 * number assigned.
	 * 
	 * @param record The record to remove.
	 */
	public void remove(R record) {
		if(this.changedRecords.contains(record)) {
			switch(this.recordStatuses.get(record)) {
				case NEW:
					this.insertNum--;
					this.changedRecords.remove(record);
					break;
				case MODIFIED:
					this.updateNum--;
					this.removeNum++;
					this.recordStatuses.put(record, RecordStatus.REMOVED);
					break;
				case REMOVED:
			}
		} else if(record.getId() != IIdentifiable.NEUTRAL_ID){
			this.changedRecords.add(record);
			this.recordStatuses.put(record, RecordStatus.REMOVED);
			this.removeNum++;
		}
	}
	
	/**
	 * Returns the set of all new records to insert.
	 * 
	 * @return Set of all new records to insert.
	 */
	public Set<R> getInsertedRecords() {
		if(this.insertNum == 0) {
			return new HashSet<>();
		}
		HashSet<R> dataSet = new HashSet<>();
		for(R record: this.changedRecords) {
			if(this.recordStatuses.get(record) == RecordStatus.NEW) {
				dataSet.add(record);
			}
		}
		return dataSet;
	}
	
	/**
	 * Returns the set of all existing records to update.
	 * 
	 * @return Set of all existing records to update.
	 */
	public Set<R> getUpdatedRecords() {
		if(this.updateNum == 0) {
			return new HashSet<>();
		}
		HashSet<R> dataSet = new HashSet<>();
		for(R record: this.changedRecords) {
			if(this.recordStatuses.get(record) == RecordStatus.MODIFIED) {
				dataSet.add(record);
			}
		}
		return dataSet;
	}
	
	/**
	 * Returns the set of all existing records to remove.
	 * 
	 * @return Set of all existing records to remove.
	 */
	public Set<R> getRemovedRecords() {
		if(this.removeNum == 0) {
			return new HashSet<>();
		}
		HashSet<R> dataSet = new HashSet<>();
		for(R record: this.changedRecords) {
			if(this.recordStatuses.get(record) == RecordStatus.REMOVED) {
				dataSet.add(record);
			}
		}
		return dataSet;
	}
}
