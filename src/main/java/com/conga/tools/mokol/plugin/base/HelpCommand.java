package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.Command;
import com.conga.tools.mokol.CommandFactory;
import com.conga.tools.mokol.Shell.CommandContext;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.StringUtil;
import com.conga.tools.mokol.SwitchDescriptor;
import com.conga.tools.mokol.SwitchDescriptor.Parameter;
import com.conga.tools.mokol.Usage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Todd Fast
 */
public class HelpCommand extends Command {

	@Override
	public void execute(CommandContext context, List<String> args)
			throws ShellException {

		Map<String,CommandFactory> commands=context.getShell().getCommands();

		if (args.size()==0) {
			context.printf("Available commands:\n");
			for (Entry<String,CommandFactory> entry: commands.entrySet()) {

				String alias=entry.getKey();

				printHelp(context,alias,entry.getValue().newInstance(context));
			}
		}
		else {
			context.printf("\n");
			for (String arg: args) {
				CommandFactory factory=commands.get(arg);
				if (factory!=null) {
					Command command=factory.newInstance(context);
					printHelp(context,arg,command);
				}
				else {
					context.printf("Unknown command \"%s\"\n",arg);
				}
			}
		}

		context.printf("\n");
	}


	/**
	 *
	 *
	 */
	public static void printHelp(CommandContext context, String alias,
			Command command) {

		context.printf(" %-20s",alias);

		Usage usage=command.getUsage(context);
		if (usage==null) {
			throw new IllegalArgumentException(
				String.format("Command \"%s\" has no usage information",
					command));
		}

		String help=usage.getShortDescription();
		if (help==null || help.trim().isEmpty())
			help="(No information available)";

		String[] helpLines=StringUtil.wrap(help,55);
		for (int i=0; i<1; i++) {
			context.printf("  %s\n",helpLines[i]);
		}

		for (int i=1; i<helpLines.length; i++) {
			context.printf(" %20s  %s\n"," ",helpLines[i]);
		}

		if (usage.getSwitchesByAbbreviation().size() > 0) {
			context.printf("\n %20s  Switches:\n\n"," ");

			for (Map.Entry<String,SwitchDescriptor> switchEntry:
					usage.getSwitchesByAbbreviation().entrySet()) {

				SwitchDescriptor descriptor=switchEntry.getValue();

				StringBuilder parameterHelp=new StringBuilder();
				List<SwitchDescriptor.Parameter> parameters=
					descriptor.getParameters();
				for (SwitchDescriptor.Parameter parameter: parameters) {
					String paramString=parameter.getHelp();
					if (paramString==null || paramString.trim().isEmpty()) {
						paramString=parameter.getType()
							.getSimpleName().toLowerCase();
					}

					if (parameterHelp.length() > 0) {
						parameterHelp.append(" ");
					}

					parameterHelp
						.append("<")
						.append(paramString)
						.append(">");
				}

				context.printf(" %20s  -%s, --%s %s\n",
					" ",
					descriptor.getAbbreviation(),descriptor.getName(),
					parameterHelp);

				if (descriptor.getHelp()!=null
						&& !descriptor.getHelp().trim().isEmpty()) {
					String[] descriptionLines=
						StringUtil.wrap(descriptor.getHelp(),52);
					for (int i=0; i<descriptionLines.length; i++) {
						context.printf(" %20s      %s\n"," ",
							descriptionLines[i]);
					}
				}
			}
		}

		context.printf("\n");
	}


	/**
	 *
	 * 
	 */
	public String getUsage() {
		return "Display this help";
	}
}
