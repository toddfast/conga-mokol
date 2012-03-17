package com.conga.tools.mokol;

import com.conga.tools.mokol.Shell.CommandContext;
import java.util.List;

/**
 *
 * @author Todd Fast
 */
public abstract class Command
{
	/**
	 *
	 *
	 * @param args
	 * @throws com.conga.platform.cassandra.Shell.ShellException
	 */
	public abstract void execute(Shell.CommandContext context,
		List<String> args)
		throws ShellException;


	/**
	 *
	 *
	 */
	protected void wrongNumberOfParameters(int min, int max, int actual) {
		throw new IllegalArgumentException("Wrong number of parameters (min="+
			min+", max="+max+", actual="+actual+")");
	}


	/**
	 *
	 *
	 */
	public Usage getUsage(CommandContext context) {
//		if (usage==null) {
//			usage=new SimpleUsage();
//
//			String help=null;
//
//			try {
//				Method method=getClass().getMethod("getUsage",new Class[0]);
//				Object methodResult=method.invoke(this,new Object[0]);
//				if (methodResult!=null)
//					help=methodResult.toString();
//			}
//			catch (NoSuchMethodException e) {
//				// Do nothing
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//				help="(Exception getting help info: "+e.getMessage()+")";
//			}
//
//			usage.setShortDescription(help);
//		}
//
//		return usage;

		if (usage==null) {
			usage=CommandIntrospector.getUsageDescriptor(
				context,this.getClass());
		}

		return usage;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private Usage usage;
}
