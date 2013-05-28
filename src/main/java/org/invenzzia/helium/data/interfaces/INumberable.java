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
 * Sometimes we might not want to maintain a global list of instances, but
 * let some entity manage its own children. These children would like to know
 * their internal number, too, so here it is the interface for them.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface INumberable {
	/**
	 * Neutral ID that should never be assigned to any object.
	 */
	public static final int NEUTRAL_ID = 0;
	/**
	 * Where to start the incrementation from?
	 */
	public static final int INCREMENTATION_START = 1;
	/**
	 * Returns the number of the object.
	 * 
	 * @return 
	 */
	public int getNumber();
	/**
	 * Sets the number of the object. The method shall throw an exception, if the
	 * number is tried to be changed.
	 * 
	 * @param id New number of the object.
	 */
	public void setNumber(int id);
}
