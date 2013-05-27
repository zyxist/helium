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

import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.helium.data.interfaces.ILightMemento;

/**
 * Simple object which allows to remember the initial state of light mementos,
 * and then restore it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StateReverter {
	/**
	 * Memory of some state.
	 */
	private Map<ILightMemento, Object> mementos;
	
	/**
	 * Remembers the state of the light memento.
	 * 
	 * @param object 
	 */
	public void remember(ILightMemento object) {
		if(null == this.mementos) {
			this.mementos = new LinkedHashMap<>();
		}
		this.mementos.put(object, object.getMemento());
	}
	
	/**
	 * Restores all the mementos.
	 */
	public void restore() {
		if(null != this.mementos) {
			for(Map.Entry<ILightMemento, Object> entry: this.mementos.entrySet()) {
				entry.getKey().restoreMemento(entry.getValue());
			}
		}
	}
}
