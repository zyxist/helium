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
import java.util.Collection;
import java.util.List;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.exception.ModelException;
import org.junit.Assert;
import org.junit.Test;

public class AbstractDataManagerTest {
	@Test
	public void testAddingItems() throws ModelException {
		DeabstractedDataManager manager = new DeabstractedDataManager();
		Item foo = new Item();
		foo.setValue("Foo");
		
		Assert.assertEquals(0, manager.size());
		manager.addItem(foo);
		Assert.assertEquals(1, manager.size());
		Assert.assertEquals(2L, manager.getNextId());
		Assert.assertEquals(1L, foo.getId());
		
		Assert.assertSame(foo, manager.findById(1L));
	}
	
	@Test
	public void testRemovingItems() throws ModelException {
		DeabstractedDataManager manager = new DeabstractedDataManager();
		Item foo = new Item();
		foo.setValue("Foo");
		Assert.assertEquals(0, manager.size());
		manager.addItem(foo);
		Assert.assertEquals(1, manager.size());
		Assert.assertEquals(2L, manager.getNextId());
		Assert.assertSame(foo, manager.findById(1));
		manager.removeItem(foo);
		Assert.assertEquals(0, manager.size());
		Assert.assertEquals(2L, manager.getNextId());
		Assert.assertNull(manager.findById(1));
	}
	
	@Test
	public void testRemovingNonexistingItemThrowsException() {
		DeabstractedDataManager manager = new DeabstractedDataManager();
		Item foo = new Item();
		foo.setValue("Foo");
		try {
			manager.removeItem(foo);
			Assert.fail("Exception not thrown.");
		} catch(ModelException exception) {
			Assert.assertEquals("The record with ID #0 does not exist.", exception.getMessage());
		}
	}
	
	@Test
	public void testFilteringItems() throws ModelException {
		DeabstractedDataManager manager = new DeabstractedDataManager();
		Item foo = new Item();
		foo.setValue("Foo");
		Item bar = new Item();
		bar.setValue("Bar");
		Item joe = new Item();
		joe.setValue("BarJoe");
		manager.addItem(foo);
		manager.addItem(bar);
		manager.addItem(joe);
		
		Collection<Item> items = manager.filter(new Predicate<Item>() {
			@Override
			public boolean apply(Item input) {
				return input.getValue().contains("Bar");
			}
		});
		Assert.assertEquals(2, items.size());
		Assert.assertTrue(items.contains(bar));
		Assert.assertTrue(items.contains(joe));
		Assert.assertFalse(items.contains(foo));
	}
	
	@Test
	public void testReturnedRecordListIsImmutable() {
		DeabstractedDataManager manager = new DeabstractedDataManager();
		List<Item> itemList = manager.getRecords();
		Assert.assertNotNull(itemList);
		try {
			itemList.add(new Item());
			Assert.fail("Exception not thrown.");
		} catch(UnsupportedOperationException exception) {
		}
	}
}


class Item implements IIdentifiable {
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
}

class DeabstractedDataManager extends AbstractDataManager<Item> {
}