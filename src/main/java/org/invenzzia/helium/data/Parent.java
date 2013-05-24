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

import java.lang.ref.WeakReference;

/**
 * In a relation, represents a reverse mapping.
 * 
 * @param R master object type.
 * @author Tomasz Jędrzejewski
 */
public final class Parent<R> {
	/**
	 * Current object.
	 */
	private R object;
	/**
	 * Weak reference to the previous object in case of modification.
	 */
	private WeakReference<R> previousObject;
	
	/**
	 * Sets the new instance.
	 * 
	 * @param newObject 
	 */
	public void set(R newObject) {
		if(null != this.object) {
			this.previousObject = new WeakReference<>(this.object);
		} else {
			this.previousObject = null;
		}
		this.object = newObject;	
	}
	
	/**
	 * Retrieves the current instance.
	 * 
	 * @return 
	 */
	public R get() {
		return this.object;
	}
	
	/**
	 * Retrieves the previous instance.
	 * 
	 * @return 
	 */
	public R getPrevious() {
		if(null != this.previousObject) {
			return this.previousObject.get();
		}
		return null;
	}
	
	/**
	 * Resets the reference to the previous instance. Not absolutely necessary, because
	 * the previous value is a weak reference, but can reduce the number of some operations.
	 */
	public void resetPrevious() {
		this.previousObject = null;
	}
	
	/**
	 * Returns true, if the instance exists.
	 * 
	 * @return True, if the instance exists.
	 */
	public boolean isDefined() {
		return null != this.object;
	}
	
	/**
	 * Returns true, if the value has been changed.
	 * 
	 * @return True, if the value has been changed.
	 */
	public boolean isChanged() {
		return null != this.previousObject && this.previousObject.get() != this.object;
	}
}
