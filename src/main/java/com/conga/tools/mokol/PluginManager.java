package com.conga.tools.mokol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Manages shell plugins
 *
 * @author Todd Fast
 */
public class PluginManager {

	/**
	 * 
	 * 
	 * @param shell
	 */
	public PluginManager(Shell shell) {
		super();
		this.shell=shell;
	}


	/**
	 *
	 *
	 */
	private Shell getShell() {
		return shell;
	}


	/**
	 *
	 *
	 */
	public void loadPlugin(Plugin plugin)
			throws ShellException {

		try {
			plugin.initialize(getShell());
			plugins.put(plugin.getName(),plugin);
		}
		catch (Exception e) {
			if (e instanceof ShellException) {
				throw (ShellException)e;
			}
			else {
				throw new ShellException(String.format(
					"Failed to initialize plugin \"%s\":",plugin.getName()),e);
			}
		}
	}


	/**
	 *
	 *
	 */
	public List<Plugin> discoverPlugins() {
		// Find all the plugins
		ServiceLoader<Plugin> pluginLoader=null;

		try {
			// Search for all fact finder implementations
			pluginLoader=ServiceLoader.load(Plugin.class);

			// Added each finder to the list for its particular profile type
			List<Plugin> result=new ArrayList<Plugin>();
			for (Plugin plugin: pluginLoader) {
				result.add(plugin);
			}

			return result;
		}
		finally {
			// Immediately clear out the cache
			if (pluginLoader!=null) {
				pluginLoader.reload();
				pluginLoader=null;
			}
		}
	}


//	/**
//	 *
//	 *
//	 */
//	public void loadAllPlugins() {
//		// Find all the plugins
//		ServiceLoader<Plugin> pluginLoader=null;
//
//		getCLI().getConsole().printf("Loading plugins...\n");
//
//		try {
//			// Search for all fact finder implementations
//			pluginLoader=ServiceLoader.load(Plugin.class);
//
//			// Added each finder to the list for its particular profile type
//			for (Plugin plugin: pluginLoader) {
//				loadPlugin(plugin);
//			}
//		}
//		finally {
//			// Immediately clear out the cache
//			if (pluginLoader!=null) {
//				pluginLoader.reload();
//				pluginLoader=null;
//			}
//
//			if (plugins.size()==0) {
//				getCLI().getConsole().printf("No plugins found.\n");
//			}
//			else {
//				getCLI().getConsole().printf("Done loading %d plugins.\n",
//					plugins.size());
//			}
//		}
//	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private Shell shell;
	private Map<String,Plugin> plugins=new HashMap<String,Plugin>();
}
