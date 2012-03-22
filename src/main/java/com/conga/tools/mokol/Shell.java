package com.conga.tools.mokol;

import com.conga.tools.mokol.plugin.base.BasePlugin;
import com.conga.tools.mokol.spi.Plugin;
import com.conga.tools.mokol.spi.Command;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import org.fusesource.jansi.Ansi;

/**
 * Mokol's command-line interface
 *
 * @author Todd Fast
 */
public class Shell implements Runnable {

	/**
	 * Can't be instantiated
	 *
	 */
	private Shell(ConsoleReader console) {
		super();
		if (console==null) {
			throw new IllegalArgumentException(
				"System console not available");
		}

		this.console=console;
	}


	/**
	 *
	 *
	 */
	/*pkg*/ PluginManager getPluginManager() {
		return pluginManager;
	}


	/**
	 *
	 *
	 */
	/*pkg*/ void setPluginManager(PluginManager value) {

		if (pluginManager!=null) {
			throw new IllegalStateException("The plugin manager has already "+
				"been set");
		}

		pluginManager=value;
	}


	/**
	 *
	 *
	 */
	public String getPromptFormat() {
		String result=prompts.peek();
		if (result==null)
			result=DEFAULT_PROMPT_FORMAT;

		return result;
	}


	/**
	 *
	 *
	 */
	public void pushPromptFormat(String value) {
		if (value!=null)
			prompts.push(value);
	}


	/**
	 *
	 *
	 */
	public String popPromptFormat() {
		if (prompts.size()==0)
			return null;
		else
			return prompts.pop();
	}


	/**
	 * Returns an unmodifiable list of alias and the associated commands
	 *
	 */
	public Map<String,CommandFactory> getCommands() {
		return Collections.unmodifiableMap(commands);
	}


	/**
	 * Add an alias for a command
	 *
	 */
	/*pkg*/ void aliasCommand(String alias, Plugin plugin,
			Class<? extends Command> command) {

		if (command==null) {
			throw new IllegalArgumentException(
				"Parameter \"command\" cannot be null");
		}

		aliasCommand(alias,plugin,new CommandClassFactory(command));
	}


	/**
	 * Add an alias for a command
	 *
	 */
	/*pkg*/ void aliasCommand(String alias, Plugin plugin,
			CommandFactory factory) {

		if (plugin==null) {
			throw new IllegalArgumentException(
				"Parameter \"plugin\" cannot be null");
		}

		if (alias==null || alias.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Parameter \"alias\" cannot be null or an empty string");
		}

		if (alias.matches("\\s")) {
			throw new IllegalArgumentException(
				"Command alias cannot contain whitespace");
		}

		if (factory==null) {
			throw new IllegalArgumentException(
				"Parameter \"factory\" cannot be null");
		}

		commands.put(alias,factory);
		commandInfos.put(alias,new CommandInfo(plugin,factory));
	}


	/**
	 *
	 *
	 */
	/*pkg*/ Plugin getPlugin(String alias) {
		return commandInfos.get(alias).getPlugin();
	}


	/**
	 * 
	 *
	 */
	public Environment getEnvironment() {
		return globalEnvironment;
	}


	/**
	 *
	 *
	 */
	public Environment getPrivateEnvironment() {
		return privateEnvironment;
	}


	/**
	 *
	 *
	 */
	/*pkg*/ ConsoleReader getConsole() {
		return console;
	}


	/**
	 *
	 *
	 */
	public boolean isEnded() {
		return ended;
	}


	/**
	 *
	 *
	 */
	public void end() {
		ended=true;
	}


	/**
	 *
	 *
	 */
	public Throwable getLastError() {
		return getPrivateEnvironment().get(ENV_LAST_ERROR,Throwable.class);
	}


	/**
	 *
	 *
	 */
	private void setLastError(Throwable value) {
		getPrivateEnvironment().put(ENV_LAST_ERROR,value);
	}


	/**
	 *
	 *
	 */
	public void executeCommand(String[] tokens) {
		String alias=null;
		List<String> args=new ArrayList<String>();
		for (String token: tokens) {
			if (token.trim().isEmpty())
				continue;

			if (alias==null)
				alias=token;
			else
				args.add(token);
		}

		if (alias==null) {
			return;
		}

		executeCommand(alias,args);
	}


	/**
	 *
	 *
	 */
	public void executeCommand(String alias, List<String> args) {

		if (alias==null || alias.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Parameter \"alias\" cannot be null or an empty string");
		}

		if (args==null) {
			args=Collections.emptyList();
		}

		// Get the command
		CommandFactory factory=getCommands().get(alias);
		if (factory==null) {
			try {
				getConsole().println(
					String.format("Unknown command \"%s\"",alias));
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			return;
		}

		executeCommand(alias,factory,args);
	}


	/**
	 *
	 *
	 */
	public void executeCommand(String alias, CommandFactory factory,
			List<String> args) {

		if (factory==null) {
			throw new IllegalArgumentException(
				"Parameter \"factory\" cannot be null");
		}

		if (args==null) {
			args=Collections.emptyList();
		}

		// Execute the command
		try {
			CommandContext context=new ShellCommandContext(this,alias);

			Command command=factory.newInstance(context);
			((CommandBase)command)._execute(context,args);
		}
		catch (Exception e) {
			setLastError(e);
			try {
				if (getPluginManager().isPluginEnabled(BasePlugin.class)) {

					String message=e.getMessage();
					if (message==null || message.trim().isEmpty())
						message=e.getClass().getName();

					getConsole().println(
						String.format("ERROR: %s (Use the \"error\" command "+
						"to see a detailed stack trace)\n",message));
				}
				else {
					// Unceremoniously dump the error to the console
					e.printStackTrace(new PrintWriter(
						getConsole().getOutput()));
				}
			}
			catch (IOException ex) {
				ex.printStackTrace(new PrintWriter(
					getConsole().getOutput()));
			}
		}
	}




	////////////////////////////////////////////////////////////////////////////
	// Main loop
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	public void run() {

		synchronized (this) {
			if (interpreterThread!=null &&
					!Thread.currentThread().equals(interpreterThread)) {
				throw new IllegalStateException("Shell cannot run in more "+
					"than a single thread");
			}

			interpreterThread=Thread.currentThread();
		}

		// Interpret commands
		String line;
		try {
			while ((line=getConsole().readLine(getFormattedPrompt()))!=null) {
				if (line.trim().isEmpty())
					continue;

				String[] tokens=line.split("\\s");

				executeCommand(tokens);

				if (isEnded())
					break;
			}
		}
		catch (IOException e) {
			System.out.println("Fatal error reading from system console");
			e.printStackTrace();
		}
	}


	/**
	 *
	 *
	 */
	protected String getFormattedPrompt() {

		// Example: "{user%s}:{count%i}";
		String prompt=getPromptFormat();

		List<String> params=new ArrayList<String>();

		boolean inParam=false;
		boolean inSpec=false;
		StringBuilder format=new StringBuilder();
		StringBuilder param=new StringBuilder();

		for (int i=0; i<prompt.length(); i++) {

			char ch=prompt.charAt(i);
			switch (ch) {
				case '{':
					if (inParam) {
						invalidPromptFormat(ch,i,prompt);
					}
					else {
						inParam=true;
					}

					break;

				case '}':
					if (!inParam) {
						invalidPromptFormat(ch,i,prompt);
					}
					else {
						inParam=false;
						inSpec=false;
						params.add(param.toString());
						param=new StringBuilder();
					}

					break;

				case '%':
					if (inSpec) {
						invalidPromptFormat(ch,i,prompt);
					}

					if (inParam) {
						inSpec=true;
					}

					format.append(ch);

					break;

				default:
					if (inParam && !inSpec)
						param.append(ch);
					else
						format.append(ch);
			}
		}

		// Take the list of params and map their values to the format string
		// using values from the environment
		Object[] args=new Object[params.size()];
		for (int i=0; i<params.size(); i++) {
			args[i]=getEnvironment().get(params.get(i),String.class);
		}

		String result=String.format(format.toString(),args);
		if (getConsole().getTerminal().isAnsiSupported()) {
			result=Ansi
				.ansi()
				.bold()
				.render(result)
				.boldOff()
				.toString();
		}

		return result;
	}


	/**
	 *
	 *
	 */
	private void invalidPromptFormat(char ch, int position, String prompt) {
		throw new IllegalArgumentException(
			"Invalid prompt format: misplaced \"%\" at position "+
			position+" of \""+prompt+"\"");
	}




	////////////////////////////////////////////////////////////////////////////
	// Main method
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	public static void main(String[] args)
			throws IOException {

        ConsoleReader console=new ConsoleReader();

		// Initialize the history file
		if (System.getProperty("mokol.history.disable")==null) {
			String historyFileLocation=System.getProperty("mokol.history",
				System.getProperty("user.home")+"/.mokol_history");

			FileHistory history=new FileHistory(new File(historyFileLocation));
			history.setMaxSize(Integer.parseInt(
				System.getProperty("mokol.history.size","100")));

			console.setHistory(history);
		}

		Shell shell=new Shell(console);

		// Header
		console.println(
			Ansi.ansi()
				.format("Hi! I'm ")
				.bold()
				.bg(Ansi.Color.YELLOW)
				.format("m")
				.bg(Ansi.Color.RED)
				.format("o")
				.bg(Ansi.Color.CYAN)
				.format("k")
				.bg(Ansi.Color.MAGENTA)
				.format("o")
				.bg(Ansi.Color.GREEN)
				.format("l")
				.fg(Ansi.Color.DEFAULT)
				.bg(Ansi.Color.DEFAULT)
				.boldOff()
			.toString());

		// Intialize the plugins
		console.println("Loading plugins...");

		PluginManager pluginManager=new PluginManager(shell);
		List<Plugin> plugins=pluginManager.discoverPlugins();

		int successCount=0;
		int failCount=0;
		for (Plugin plugin: plugins) {
			String format="Initializing plugin \"%s\" [%s %s]...";
			if (plugin.getName()==null || plugin.getName().trim().isEmpty()) {
				format="Initializing plugin [%2$s, %3$s]...";
			}

			// Print initialization header
			shell.getConsole().print(
				String.format(format,
					Ansi.ansi()
						.bold()
						.format(plugin.getName())
						.boldOff()
						.toString(),
					plugin.getClass().getName(),
					plugin.getVersion()));

			try {
				// Load the plugin
				pluginManager.loadPlugin(plugin);

				successCount++;
				shell.getConsole().println(
					Ansi.ansi()
						.fg(Ansi.Color.GREEN)
						.format("ok")
						.fg(Ansi.Color.DEFAULT)
						.toString());
			}
			catch (Throwable e) {
				// Failed
				failCount++;
				shell.getConsole().println(
					Ansi.ansi()
						.fg(Ansi.Color.RED)
						.format("fail")
						.fg(Ansi.Color.DEFAULT)
						.toString());

				String message=
					Ansi.ansi()
						.fg(Ansi.Color.RED)
						.format("Plugin failed to intialize:")
						.fg(Ansi.Color.DEFAULT)
						.toString();

				shell.getConsole().println(message);
				e.printStackTrace(new PrintWriter(
					shell.getConsole().getOutput()));
			}
		}

		if (plugins.size()==0) {
			console.println("Oops, no plugins found. This is gonna be boring.");
		}
		else
		if (failCount==0) {
			console.println("All plugins loaded successfully.");
		}
		else {
			console.println(String.format(
				"%d plugin%s loaded successfully. %d plugins failed.",
				successCount,(successCount!=1 ? "s" : ""),
				failCount,(failCount!=1 ? "s" : "")));
		}

		console.println();

		shell.setPluginManager(pluginManager);

		// Execute the initial command
		shell.executeCommand(args);

		if (!shell.isEnded()) {
			// Run the shell on a separate thread
			Thread thread=new Thread(shell);
			thread.setDaemon(true);
			thread.start();

			try {
				// Wait until the shell thread exits
				thread.join();
			}
			catch (InterruptedException e) {
				// Graceful close
			}
		}

		console.println("Bye.");
	}




	////////////////////////////////////////////////////////////////////////////
	// Inner types
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	private class CommandInfo {
		public CommandInfo(Plugin plugin, CommandFactory factory) {
			super();
			this.plugin=plugin;
			this.factory=factory;
		}

		/**
		 *
		 *
		 */
		public Plugin getPlugin() {
			return plugin;
		}

		/**
		 *
		 *
		 */
		public CommandFactory getFactory() {
			return factory;
		}

		private Plugin plugin;
		private CommandFactory factory;
	}


	/**
	 *
	 *
	 */
	private static class ShellCommandContext implements CommandContext {

		/**
		 *
		 *
		 */
		protected ShellCommandContext(Shell instance, String commandAlias) {
			super();
			this.shell=instance;
			this.commandAlias=commandAlias;
		}

		/**
		 *
		 *
		 */
		@Override
		public Shell getShell() {
			return shell;
		}

		/**
		 *
		 *
		 */
		@Override
		public String getCommandAlias() {
			return commandAlias;
		}

		/**
		 *
		 *
		 */
		@Override
		public String readLine() {
			try {
				return shell.getConsole().readLine();
			}
			catch (IOException e) {
				throw new RuntimeException("Error reading line "+
					"from system console",e);
			}
		}

		/**
		 *
		 *
		 */
		@Override
		public String readLine(String format, Object... args) {
			try {
				return shell.getConsole().readLine(String.format(format,args));
			}
			catch (IOException e) {
				throw new RuntimeException("Error reading line "+
					"from system console",e);
			}
		}

		/**
		 *
		 *
		 */
		@Override
		public String readPassword() {
			try {
				return shell.getConsole().readLine('*');
			}
			catch (IOException e) {
				throw new RuntimeException("Error reading line "+
					"from system console",e);
			}
		}

		/**
		 *
		 *
		 */
		@Override
		public String readPassword(String format, Object... args) {
			try {
				return shell.getConsole().readLine(String.format(format,args));
			}
			catch (IOException e) {
				throw new RuntimeException("Error reading line "+
					"from system console",e);
			}
		}

		/**
		 *
		 *
		 */
		@Override
		public PrintWriter writer() {

			return writer;
		}

		/**
		 *
		 *
		 */
		@Override
		public CommandContext printf(String format, Object... args) {
			try {
				shell.getConsole().print(String.format(format,args));
				shell.getConsole().flush();
			}
			catch (IOException e) {
				throw new RuntimeException("Error writing line "+
					"to system console",e);
			}

			return this;
		}

		private Shell shell;
		private String commandAlias;
		private PrintWriter writer=new PrintWriter(System.out,true);
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private static final String ENV_LAST_ERROR="lastError";
	private static final String DEFAULT_PROMPT_FORMAT="[mokol] ";

	private Thread interpreterThread;
	private ConsoleReader console;
	private PluginManager pluginManager;
	private boolean ended;
	private Deque<String> prompts=new ArrayDeque<String>();
	private Map<String,CommandFactory> commands=
		Collections.synchronizedMap(
			new TreeMap<String,CommandFactory>());
	private Map<String,CommandInfo> commandInfos=
		Collections.synchronizedMap(
			new TreeMap<String,CommandInfo>());
	private Environment globalEnvironment=new Environment(this);
	private Environment privateEnvironment=new Environment(this);
}
