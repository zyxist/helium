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

import java.util.Set;
import junit.framework.Assert;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.junit.Test;

public class UnitOfWorkTest {
	@Test
	public void testInsertingNewItemsWorks() {
		UnitOfWork<UnitItem> unit = new UnitOfWork<>();
		
		UnitItem item = new UnitItem();
		item.setValue("Foo");
		
		Assert.assertTrue(unit.isEmpty());
		Assert.assertFalse(unit.isUpdatingExistingState());
		unit.insert(item);
		Assert.assertFalse(unit.isEmpty());
		Assert.assertFalse(unit.isUpdatingExistingState());
		
		Set<UnitItem> insertedItems = unit.getInsertedRecords();
		Assert.assertEquals(1, insertedItems.size());
		Assert.assertTrue(insertedItems.contains(item));
	}
	
	@Test
	public void testInsertingOnTheSameItemIsPermittedWithoutSideEffects() {
		UnitOfWork<UnitItem> unit = new UnitOfWork<>();
		
		UnitItem item = new UnitItem();
		item.setValue("Foo");
		
		Assert.assertTrue(unit.isEmpty());
		Assert.assertFalse(unit.isUpdatingExistingState());
		unit.insert(item);
		unit.insert(item);
		Assert.assertFalse(unit.isEmpty());
		Assert.assertFalse(unit.isUpdatingExistingState());
		
		Set<UnitItem> insertedItems = unit.getInsertedRecords();
		Assert.assertEquals(1, insertedItems.size());
		Assert.assertTrue(insertedItems.contains(item));
	}
	
	@Test
	public void testCannotInsertExistingItem() {
		UnitOfWork<UnitItem> unit = new UnitOfWork<>();
		
		UnitItem item = new UnitItem();
		item.setValue("Foo");
		item.setId(345);
		
		try {
			unit.insert(item);
		} catch(IllegalArgumentException exception) {
			Assert.assertEquals("The record 'Foo' is already in the data model and cannot be inserted again.", exception.getMessage());
		}
	}
}

class UnitItem implements IIdentifiable {
	private long id = IIdentifiable.NEUTRAL_ID;
	private String value;

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}