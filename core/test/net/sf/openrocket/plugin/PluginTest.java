package net.sf.openrocket.plugin;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test the plugin loading system using Guice.
 * 
 * This is more of an integration than a unit test.  It uses the
 * PluginModule to load a Guice injector, and then verifies that it
 * has found the appropriate plugins.
 */
public class PluginTest {
	
	@Test
	public void testPluginModule() {
		
		Injector injector = Guice.createInjector(new PluginModule());
		PluginTester tester = injector.getInstance(PluginTester.class);
		tester.testPlugins();
		
	}
	
}
