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
 * A data record that has a unique numeric ID.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IIdentifiable {
	/**
	 * Neutral ID that should never be assigned to any object.
	 */
	public static final long NEUTRAL_ID = 0;
	/**
	 * Where to start the incrementation from?
	 */
	public static final long INCREMENTATION_START = 1;
	/**
	 * Returns the ID of the object.
	 * 
	 * @return 
	 */
	public long getId();
	/**
	 * Sets the ID of the object. The method shall throw an exception, if the
	 * ID is tried to be changed.
	 * 
	 * @param id New ID of the object.
	 */
	public void setId(long id);
}
