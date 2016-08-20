package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompilationPackage;
import com.github.picadoh.imc.model.CompilationUnit;
import com.github.picadoh.imc.model.JavaMemoryObject;
import com.github.picadoh.imc.model.JavaSourceFromString;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * The dynamic compiler uses the JavaCompiler with custom implementations of a JavaFileManager and
 * JavaFileObject to compile a Java Source from a String to Bytecode.
 * 
 * @see InMemoryClassManager
 * @see JavaSourceFromString
 * @see JavaMemoryObject
 */
public class InMemoryCompiler {

	/**
	 * Compiles a single class.
	 *
	 * <pre>
	 * Example code:
	 * <code>
	 * try {
	 *  DynamicCompiler compiler = new DynamicCompiler();
	 * 	CompilationPackage pkg = compiler.singleCompile(
	 * 		"public class HelloWorld {\n"+
	 * 		"	public static void main(String[] args) {\n"+
	 * 		"		System.out.println(\"hello,world!\");\n"+
	 * 		"	}\n"+
	 * 		"}\n");
	 * }
	 * catch(CompilerException e) {
	 * 	// face it or throw it away
	 * }
	 * </code>
	 * </pre>
	 *
	 * @param className Ruleset name
	 * @param code Rule class code
	 * @return Compilation package
	 * @throws CompilerException Thrown when a compiler exception occurs
	 */
	public CompilationPackage singleCompile(String className, String code) throws CompilerException {
		return compile(ImmutableMap.<String, String>builder()
						.put(className, code)
						.build()
		);
	}

	/**
	 * Given a map of class FQDN and its source code, this method compiles the code and
	 * returns a Compilation Package that encapsulates it.
	 *
	 * @param classesToCompile Map of className/classSource to compile
	 * @return Compilation package
	 * @throws CompilerException
	 *             Thrown when a compiler exception occurs
	 */
	public CompilationPackage compile(Map<String, String> classesToCompile)
			throws CompilerException {

		JavaCompiler compiler = getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> collector = getDiagnosticCollector();
		InMemoryClassManager manager = getClassManager(compiler);
		
		// defining classpath
		String classpath = loadClasspath();

		// add classpath to options
		List<String> options = Arrays.asList("-classpath", classpath);

		// java source from string
		List<JavaSourceFromString> strFiles = newArrayList();
		for (String className : classesToCompile.keySet()) {
			String classCode = classesToCompile.get(className);
			strFiles.add(new JavaSourceFromString(className, classCode) );
		}

		// compile
		CompilationTask task = compiler.getTask(null, manager, collector, options, null, strFiles);
		boolean status = task.call();

		// check for compilation errors
		if (status) {
			List<CompilationUnit> compilationUnits = manager.getAllClasses();
			return new CompilationPackage(compilationUnits);
		} else {
			// something's really wrong
			String compilationReport = buildCompilationReport(collector, options);
			throw new CompilerException(compilationReport);
		}
	}

	@VisibleForTesting
	DiagnosticCollector<JavaFileObject> getDiagnosticCollector() {
		return new DiagnosticCollector<>();
	}

	@VisibleForTesting
	InMemoryClassManager getClassManager(JavaCompiler compiler) {
		return new InMemoryClassManager(compiler.getStandardFileManager(null, null, null));
	}

	@VisibleForTesting
	JavaCompiler getSystemJavaCompiler() {
		return ToolProvider.getSystemJavaCompiler();
	}

	@VisibleForTesting
	String buildCompilationReport(DiagnosticCollector<JavaFileObject> collector, List<String> options) {
		int count = 0;
		StringBuilder resultBuilder = new StringBuilder();

		// group diagnostic by class
		for (Diagnostic<?> diagnostic : collector.getDiagnostics()) {
			count++;

			JavaSourceFromString javaSource = (JavaSourceFromString)diagnostic.getSource();

			resultBuilder.append(javaSource.getCharContent(false)).append("\n");
			resultBuilder.append("Compiler options: ").append(options).append("\n\n");
			resultBuilder.append(diagnostic.getKind()).append("|").append(diagnostic.getCode()).append("\n");
			resultBuilder.append("LINE:COLUMN ")
					.append(diagnostic.getLineNumber())
					.append(":")
					.append(diagnostic.getColumnNumber())
					.append("\n")
					.append(diagnostic.getMessage(null))
					.append("\n\n");
		}

		String diagnosticString = resultBuilder.toString();
		String compilationErrorsOverview = String.valueOf(count) + " class(es) failed to compile";

		return "Compilation error\n" + compilationErrorsOverview + "\n" + diagnosticString;
	}

	@VisibleForTesting
	String loadClasspath() {
		StringBuilder sb = new StringBuilder();
		URLClassLoader urlClassLoader = (URLClassLoader) Thread
				.currentThread().getContextClassLoader();
		for (URL url : urlClassLoader.getURLs()) {
			sb.append(url.getFile()).append(
					System.getProperty("path.separator"));
		}

		return sb.toString();
	}

	public static class CompilerException extends Exception {

		/**
		 * Creates a new compiler exception.
		 * @param message Message.
		 */
		public CompilerException(String message) {
			super(message);
		}

	}
}
