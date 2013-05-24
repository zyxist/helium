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
package org.invenzzia.helium.data.utils;

import com.google.common.base.Preconditions;

/**
 * Utilities for writing mementos that reduce the number of boilerplate
 * code.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MementoUtils {
	private MementoUtils() {
	}
	
	/**
	 * Verifies the preconditions: memento is not null and has the correct class.
	 * 
	 * @param memento Memento object to check.
	 * @param expectedMementoType Expected type of this object.
	 * @param primaryType The class, that the memento is supposed to keep the state of.
	 * @return Correctly casted object.
	 */
	public static <T> T checkMemento(Object memento, Class<T> expectedMementoType, Class<?> primaryType) {
		Preconditions.checkNotNull(memento, "The memento is NULL!");
		if(!expectedMementoType.isAssignableFrom(memento.getClass())) {
			throw new IllegalArgumentException("Invalid memento for "+primaryType.getSimpleName()+" class: "+memento.getClass().getCanonicalName());
		}
		return (T) memento;
	}
}
