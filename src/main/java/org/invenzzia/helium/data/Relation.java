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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.invenzzia.helium.exception.ModelException;

/**
 * Represents a relation in the data model.
 * 
 * @param R type of related records.
 * @author Tomasz Jędrzejewski
 */
public class Relation<R> {
	/**
	 * Map of all related objects.
	 */
	private Set<R> relatives;
	
	public Relation() {
		this.relatives = new LinkedHashSet<>();
	}

	/**
	 * Allows checking if the given record is attached to this relation.
	 * 
	 * @param object Object to check.
	 * @return True, if that object is in this relation.
	 */
	public boolean isAttached(R object) {
		return this.relatives.contains(object);
	}

	/**
	 * Attaches a new object to the relation. The reverse mapping must be done manually.
	 * 
	 * @param object
	 * @throws ModelException 
	 */
	public void attach(R object) throws ModelException {
		if(this.relatives.contains(object)) {
			throw new ModelException("The record '"+object.toString()+"' is already attached to this relation.");
		}
		this.relatives.add(object);
	}
	
	/**
	 * Detaches an object from the relation. The reverse mapping must be updated manually.
	 * 
	 * @param object
	 * @throws ModelException 
	 */
	public void detach(R object) throws ModelException {
		if(!this.relatives.contains(object)) {
			throw new ModelException("The record '"+object.toString()+"' cannot be detached: not attached.");
		}
		this.relatives.remove(object);
	}
	
	/**
	 * Returns the number of related objects.
	 * 
	 * @return Number of related objects.
	 */
	public int size() {
		return this.relatives.size();
	}
	
	/**
	 * Returns true, if the relation is empty.
	 * 
	 * @return Whether this relation is empty.
	 */
	public boolean isEmpty() {
		return this.relatives.isEmpty();
	}
	
	/**
	 * Returns an immutable collection of all related objects.
	 * 
	 * @return All related objects.
	 */
	public Collection<R> getRelated() {
		return ImmutableSet.copyOf(this.relatives);
	}
	
	/**
	 * Returns a collection of all the related objects that satisfy the given
	 * predicate. The returned collection may be empty.
	 * 
	 * @param predicate Predicate that represents a condition applied to every object in the relation.
	 * @return List of all objects that satisfy the predicate.
	 */
	public Collection<R> filter(Predicate<R> predicate) {
		HashSet<R> bound = new HashSet<>();
		for(R relative: this.relatives) {
			if(predicate.apply(relative)) {
				bound.add(relative);
			}
		}
		return bound;
	}
}
