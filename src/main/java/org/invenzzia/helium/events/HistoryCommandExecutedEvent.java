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
package org.invenzzia.helium.events;

import org.invenzzia.helium.history.History;

/**
 * This event is emitted only if the new command is executed. This allows distinguishing
 * certain types of events one from another.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HistoryCommandExecutedEvent<T> extends HistoryChangedEvent<T> {
	public HistoryCommandExecutedEvent(History<T> history) {
		super(history);
	}
}
