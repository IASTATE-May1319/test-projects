# README #
================

## About ##

This is a small demo of using jAtlas for simple null type checking.

## Current Limitations ##

* Currently, this solution recursively checks the dataflow through the app to see if the type `org.eclipse.jdt.core.dom.NullLiteral` is ever assigned to a variable that flows to the node passed in.  As far as I know, this will not detect the case where an instance variable is not initialized to anything and then gets assigned to the variable being checked.

	public class Test {
		String notInitialized;
		
		public void performTest(@NonNull String a) {
			// Do something that assumes parameter is NonNull
		}
		
		public void test() {
			// Pass in the unitialized instance variable into performTest
			performTest(notInitialized);
		}
	}

So in this example, the flow from method `test()` to `performTest(@NonNull String a)` would not be caught. This is because the type jAtlas sees for `notInitialized` is not `org.eclipse.jdt.core.dom.NullLiteral`, but is something else.

* Another limitation it currently has is that once it finds a flow from `null` to the sink node, it stops looking. Therefore, it will only display at most one offense.

## Running this Solution ##

Prereqs: This tutorial assumes that you already have Eclipse set up with the jAtlas plugin, and the `edu.iastate.apac.toolbox` project added to your Eclipse workspace.

1. Checkout the project from Github
2. Import the `Nullable` project into eclipse
	* This is just a Java project with a couple classes
	* `Main.java` is the file with the simple test
	* `NonNull.java` just defines the `@NonNull` annotation. It doesn't currently do anything, but just provides an indication of what types we are wanting to be NonNull
3. Add the `NullChecker.scala` file to the `toolbox.script` package in the `edu.iastate.apac.toolbox` project.
	* What I did was just dragged it into the package in Eclipse, and when Eclipse prompted asking if I wanted to copy the file or link the file, I chose link. This way the file stays in the repository folder, and also is in the right place in the Eclipse project.
4. Reindex the workspace (I just had the `edu.iastate.apac.toolbox` project and `Nullable` project open)
5. Restart the `J-Atlas Interpreter`
6. Run `show(NullChecker.check(methods("c")))` in the J-Atlas Interpreter

### Explanation of the Command ###

The command `show(NullChecker.check(methods("c")))` shows the resulting flow from the null assignment to the method named `c`.  If no null assignment happens, no graph will show up (though just running on the provided example project, you should get a graph).

Note that this is most likely not how we will want to select the "sink" of the flow. Selecting the method named "c" is really just a simple way to prove the point, but in the future this will likely be based on provided rules, or on an annotation (`@NonNull` for example).
