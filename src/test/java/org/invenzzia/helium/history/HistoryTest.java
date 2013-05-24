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
package org.invenzzia.helium.history;

import com.google.common.eventbus.EventBus;
import java.util.List;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HistoryTest {
	@Test
	public void testExecutingCommandsCreatesPastHistory() throws CommandExecutionException, Exception {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd = mock(ICommand.class);
		
		Assert.assertFalse(history.hasPastOperations());
		Assert.assertFalse(history.hasFutureOperations());
		
		history.execute(cmd);
		
		Assert.assertTrue(history.hasPastOperations());
		Assert.assertFalse(history.hasFutureOperations());
		
		verify(cmd).execute();
	}

	@Test
	public void testUndoWorks() throws CommandExecutionException, Exception {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd = mock(ICommand.class);
		history.execute(cmd);
		verify(cmd).execute();
		history.undo();
		Assert.assertFalse(history.hasPastOperations());
		Assert.assertTrue(history.hasFutureOperations());
		verify(cmd).undo();
	}
	
	@Test
	public void testRedoWorks() throws CommandExecutionException, Exception {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd = mock(ICommand.class);
		history.execute(cmd);
		verify(cmd).execute();
		history.undo();
		verify(cmd).undo();
		history.redo();
		verify(cmd).redo();
		Assert.assertTrue(history.hasPastOperations());
		Assert.assertFalse(history.hasFutureOperations());
	}
	
	@Test
	public void testFailedUndoInvalidatesHistory() throws CommandExecutionException, Exception {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd = mock(ICommand.class);
		ICommand failingCmd = mock(ICommand.class);
		RuntimeException x = new RuntimeException("Foo");
		doThrow(x).when(failingCmd).undo();
		

		history.execute(cmd);
		history.execute(failingCmd);
		verify(cmd).execute();
		verify(failingCmd).execute();

		try {
			history.undo();
			Assert.fail("Exception not thrown");
		} catch(CommandExecutionException exception) {
			Assert.assertSame(x, exception.getCause());
		}
		Assert.assertFalse(history.hasPastOperations());
		Assert.assertFalse(history.hasFutureOperations());
	}
	
	@Test
	public void testFailedRedoInvalidatesHistory() throws CommandExecutionException, Exception {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd = mock(ICommand.class);
		ICommand failingCmd = mock(ICommand.class);
		ICommand lastCmd = mock(ICommand.class);
		RuntimeException x = new RuntimeException("Foo");
		
		history.execute(cmd);
		history.execute(failingCmd);
		history.execute(lastCmd);
		history.undo();
		verify(lastCmd).undo();
		history.undo();
		verify(failingCmd).undo();
		Assert.assertTrue(history.hasFutureOperations());

		try {
			doThrow(x).when(failingCmd).redo();
			history.redo();
			Assert.fail("Exception not thrown");
		} catch(CommandExecutionException exception) {
			Assert.assertSame(x, exception.getCause());
		}
		Assert.assertFalse(history.hasPastOperations());
		Assert.assertFalse(history.hasFutureOperations());
	}
	
	@Test
	public void testReturningInfoAboutPastHistory() throws CommandExecutionException {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd1 = new HistoryTest.SampleCmd1();
		ICommand cmd2 = new HistoryTest.SampleCmd2();
		
		history.execute(cmd1);
		history.execute(cmd2);
		
		List<CommandInfo<ICommand>> info = history.getHistory();
		Assert.assertEquals(3, info.size());
		
		Assert.assertEquals("Base command", info.get(0).getName());
		Assert.assertFalse(info.get(0).isFuture());
		Assert.assertTrue(info.get(0).isBase());
		
		Assert.assertEquals("Sample command 1", info.get(1).getName());
		Assert.assertEquals(HistoryTest.SampleCmd1.class, info.get(1).getType());
		Assert.assertFalse(info.get(1).isFuture());
		Assert.assertTrue(info.get(1).acceptsCommand(cmd1));
		Assert.assertFalse(info.get(1).acceptsCommand(cmd2));
		Assert.assertFalse(info.get(1).isBase());
		
		Assert.assertEquals("SampleCmd2", info.get(2).getName());
		Assert.assertEquals(HistoryTest.SampleCmd2.class, info.get(2).getType());
		Assert.assertFalse(info.get(2).isFuture());
		Assert.assertTrue(info.get(2).acceptsCommand(cmd2));
		Assert.assertFalse(info.get(2).acceptsCommand(cmd1));
		Assert.assertFalse(info.get(2).isBase());
	}
	
	@Test
	public void testReturningInfoAboutFutureHistory() throws CommandExecutionException {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(100);
		ICommand cmd1 = new HistoryTest.SampleCmd1();
		ICommand cmd2 = new HistoryTest.SampleCmd2();
		
		history.execute(cmd1);
		history.execute(cmd2);
		history.undo();
		history.undo();
		
		List<CommandInfo<ICommand>> info = history.getHistory();
		Assert.assertEquals(3, info.size());
		Assert.assertEquals("Base command", info.get(0).getName());
		Assert.assertFalse(info.get(0).isFuture());
		Assert.assertTrue(info.get(0).isBase());

		Assert.assertEquals("Sample command 1", info.get(1).getName());
		Assert.assertEquals(HistoryTest.SampleCmd1.class, info.get(1).getType());
		Assert.assertTrue(info.get(1).isFuture());
		Assert.assertTrue(info.get(1).acceptsCommand(cmd1));
		Assert.assertFalse(info.get(1).acceptsCommand(cmd2));
		Assert.assertFalse(info.get(1).isBase());
		
		Assert.assertEquals("SampleCmd2", info.get(2).getName());
		Assert.assertEquals(HistoryTest.SampleCmd2.class, info.get(2).getType());
		Assert.assertTrue(info.get(2).isFuture());
		Assert.assertTrue(info.get(2).acceptsCommand(cmd2));
		Assert.assertFalse(info.get(2).acceptsCommand(cmd1));
		Assert.assertFalse(info.get(2).isBase());
	}
	
	@Test
	public void testOldHistoryIsDiscarded() throws CommandExecutionException {
		History<ICommand> history = new History<>(new TestHistoryStrategy(), mock(EventBus.class));
		history.setMaximumCapacity(2);
		
		ICommand cmd1 = mock(ICommand.class);
		ICommand cmd2 = mock(ICommand.class);
		ICommand cmd3 = mock(ICommand.class);
		final StringBuilder bld = new StringBuilder();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				bld.append("c");
				return null;
			}
		}).when(cmd3).undo();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				bld.append("b");
				return null;
			}
		}).when(cmd2).undo();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				bld.append("a");
				return null;
			}
		}).when(cmd1).undo();
		
		history.execute(cmd1);
		history.execute(cmd2);
		history.execute(cmd3);
		Assert.assertTrue(history.hasPastOperations());
		history.undo();
		history.undo();
		Assert.assertFalse(history.hasPastOperations());
		history.undo();
		Assert.assertFalse(history.hasPastOperations());
		Assert.assertEquals("cb", bld.toString());
	}
	
	@CommandDetails(name = "Sample command 1")
	public static class SampleCmd1 implements ICommand {
		@Override
		public void execute() throws Exception {
		}

		@Override
		public void undo() {
		}
		
		@Override
		public void redo() {
		}
	}
	
	public static class SampleCmd2 implements ICommand {
		@Override
		public void execute() throws Exception {
		}

		@Override
		public void undo() {
		}

		@Override
		public void redo() {
		}
	}
}

@CommandDetails(name = "Base command")
class BaseCommand implements ICommand {

	@Override
	public void execute() throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void undo() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void redo() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}	
}

interface ICommand {
	public void execute() throws Exception;
	public void undo();
	public void redo();
}

class TestHistoryStrategy implements IHistoryStrategy<ICommand> {
	@Override
	public void execute(ICommand command) throws Exception {
		command.execute();
	}

	@Override
	public void undo(ICommand command) {
		command.undo();
	}

	@Override
	public void redo(ICommand command) {
		command.redo();
	}

	@Override
	public ICommand getBaseCommand() {
		return new BaseCommand();
	}
}
