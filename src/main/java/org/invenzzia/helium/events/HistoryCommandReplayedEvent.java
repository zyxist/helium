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
 * This event is emitted only if an 'undo' or 'redo' action was performed on some
 * command or commands. Most listeners would be interested in this case only.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HistoryCommandReplayedEvent<T> extends HistoryChangedEvent<T> {
	public HistoryCommandReplayedEvent(History<T> history) {
		super(history);
	}
}
