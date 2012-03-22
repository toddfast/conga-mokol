package com.conga.tools.mokol;

import com.conga.tools.mokol.metadata.CommandDescriptor;
import com.conga.tools.mokol.spi.Plugin;
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
/*pkg*/ class PluginManager {

	/**
	 *
	 *
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
			Map<String,CommandDescriptor> descriptors=
				((PluginBase)plugin)._initialize(getShell());

			// Alias all the commands
			// TODO: Check for conflicting aliases and give a chance to re-alias
			for (Map.Entry<String,CommandDescriptor> entry: 
					descriptors.entrySet()) {
				getShell().aliasCommand(entry.getValue().getAlias(),
					plugin,entry.getValue().getFactory());
			}

			plugins.put(plugin.getClass(),plugin);
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
	public boolean isPluginEnabled(Class<? extends Plugin> pluginClass) {
		return plugins.keySet().contains(pluginClass);
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
	private Map<Class<? extends Plugin>,Plugin> plugins=
		new HashMap<Class<? extends Plugin>,Plugin>();
}
