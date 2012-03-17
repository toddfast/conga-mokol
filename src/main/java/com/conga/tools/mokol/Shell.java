package com.conga.tools.mokol;

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
 * Command-line interface
 *
 * @author Todd Fast
 */
public class Shell implements Runnable {

//	/**
//	 * Can't be instantiated
//	 *
//	 */
//	private Shell(Console console) {
//		super();
//		if (console==null) {
//			throw new IllegalArgumentException(
//				"System console not available");
//		}
//
//		this.console=console;
//	}


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
	public PluginManager getPluginManager() {
		return pluginManager;
	}


	/**
	 *
	 *
	 */
	public void setPluginManager(PluginManager value) {

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


//	/**
//	 *
//	 *
//	 */
//	public void setPromptFormat(String value) {
//		if (value==null)
//			promptFormat=DEFAULT_PROMPT_FORMAT;
//		else
//			promptFormat=value;
//	}


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
	public void aliasCommand(String alias, Class<? extends Command> command) {

		if (command==null) {
			throw new IllegalArgumentException(
				"Parameter \"command\" cannot be null");
		}

		aliasCommand(alias,new CommandClassFactory(command));
	}


	/**
	 * Add an alias for a command
	 *
	 */
	public void aliasCommand(String alias, CommandFactory factory) {

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
	}


	/**
	 * Returns an unmodifiable list of the environment objects
	 *
	 */
	public Map<String,Object> getEnvironment() {
		return environment;
	}


	/**
	 *
	 *
	 */
	public <T> T getEnvironmentValue(String name, Class<? extends T> clazz) {
		return clazz.cast(getEnvironment().get(name));
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

//				getConsole().printf("echo: %s\n",token);
		}

		if (alias==null) {
//			getConsole().printf("Invalid command\n");
//			alias="help";
//			args=Collections.emptyList();
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
//			getConsole().printf("Unknown command \"%s\"\n",alias);
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
			CommandContext context=new CommandContext(this,alias);

			Command command=factory.newInstance(context);
			command.execute(context,args);
		}
		catch (Exception e) {
			getEnvironment().put(Environment.ENV_LAST_ERROR,e);
//			getConsole().printf("ERROR: %s (Use the \"error\" command "+
//				"to see a detailed stack trace)\n\n",e.getMessage());
			try {
				getConsole().println(
					String.format("ERROR: %s (Use the \"error\" command "+
					"to see a detailed stack trace)\n",e.getMessage()));
			}
			catch (IOException ex) {
				ex.printStackTrace();
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
	@Override
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
		Map<String,Object> env=getEnvironment();

		Object[] args=new Object[params.size()];
		for (int i=0; i<params.size(); i++) {
			args[i]=env.get(params.get(i));
		}

		String result=String.format(format.toString(),args);
		if (getConsole().getTerminal().isAnsiSupported()) {
//			result=new AnsiBuffer().bold(result).getAnsiBuffer();
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

		// Intialize the plugins
		console.println("Loading plugins...");

		PluginManager pluginManager=new PluginManager(shell);
		List<Plugin> plugins=pluginManager.discoverPlugins();
		for (Plugin plugin: plugins) {
			shell.getConsole().println(
				String.format("Initializing plugin \"%s\" (%s)",
					plugin.getName(),plugin.getVersion()));

			try {
				pluginManager.loadPlugin(plugin);
			}
			catch (Exception e) {
				String message=String.format(
					"Failed to initialize plugin \"%s\":",plugin.getName());

				shell.getConsole().println(message);
				e.printStackTrace();
			}
		}

		if (plugins.size()==0) {
			console.println("No plugins found.");
		}
		else {
			console.println(String.format("Done loading %d plugins.",
				plugins.size()));
		}

		shell.setPluginManager(pluginManager);

		// Execute the initial command
		shell.executeCommand(args);

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

		console.println("Bye.");
	}




	////////////////////////////////////////////////////////////////////////////
	// Inner types
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	public static class CommandContext {

		/**
		 *
		 *
		 */
		protected CommandContext(Shell instance, String commandAlias) {
			super();
			this.shell=instance;
			this.commandAlias=commandAlias;
		}

		/**
		 *
		 *
		 */
		public Shell getShell() {
			return shell;
		}

		/**
		 *
		 *
		 */
		public String getCommandAlias() {
			return commandAlias;
		}

		/**
		 *
		 *
		 */
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
		public PrintWriter writer() {

			return writer;
		}

		/**
		 *
		 *
		 */
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

	private static final String DEFAULT_PROMPT_FORMAT="[mokol] ";

	private Thread interpreterThread;
	private ConsoleReader console;
	private PluginManager pluginManager;
	private boolean ended;
	private Deque<String> prompts=new ArrayDeque<String>();
	private Map<String,CommandFactory> commands=
		Collections.synchronizedMap(
			new TreeMap<String,CommandFactory>());
	private Map<String,Object> environment=Collections.synchronizedMap(
		new TreeMap<String,Object>());
}
