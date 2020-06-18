### In-Memory Java Compiler
The in-memory compiler allows you to compile java source to in-memory bytecode.

### Disclaimer

This code isn't considered stable thus background compatibility isn't guaranteed.

* APIs are subject to change at any time.
* Code makes use of internal Java APIs that may change between Java versions.

### API Documentation

**Compile**

    InMemoryJavaCompiler::compile(className, classSourceCode)
        returns CompilerResult

    InMemoryJavaCompiler::compile(classSourceMap)
        returns CompilerResult

**Compilation Results**

    CompilerResult::hasErrors()
        returns boolean

    CompilerResult::getCompilationErrorReport()
        returns CompilationErrorReport

    CompilerResult::loadClassMap()
        returns Map<String,Class<?>>
        throws ClassNotFoundException

**Compilation Error Report**

    CompilationErrorReport::getOptions
        returns List<String>

    CompilationErrorReport::getErrors
        returns List<CompilationError>

    CompilationErrorReport::toString
        returns String // default pretty print format

### Samples

#### Basic Sample

    String source =
        "public class Main {\n"+
            "public static void main(String[] args) {\n"+
                "System.out.println(\"hello,world!\");\n"+
            "}\n"+
        "}";

    // Compile
    InMemoryJavaCompiler compiler = new InMemoryJavaCompiler();
    CompilerResult result = compiler.compile("Main", source);

    // Load
    Map<String, Class<?>> classes = result.loadClassMap();

    // Execute (using Reflection)
    classes.get("HelloWorld").getMethod("main", String[].class).invoke(null, (Object) null);

#### Handling Compiler Errors

The `CompilerResult` may include compilation errors, if any.

    InMemoryJavaCompiler compiler = new InMemoryJavaCompiler();
    CompilerResult result = compiler.compile("Main", source);

    if (result.hasErrors()) {
        System.out.println(result.getCompilationErrorReport());
    } else {
        // ...
    }

#### Using a known interface

If the subject source code implements a known interface to the application executing the compiler (e.g. present in the classpath), it will be considered during compilation and can be reused to execute the code instead of using reflection, as in the below example.

**Interface**

    public interface StringSorter {
        void sort(List<String> strings);
    }

**Subject code**

    String source =
        "import java.util.Collections;\n"+
        "import java.util.List;\n"+
        "public class StringSorterByText implements StringSorter {\n"+
            "@Override\n"+
            "public void sort(List<String> strings) {\n"+
                "Collections.sort(strings);\n"+
            "}\n"+
        "}";

**Usage**

    // Compile
    InMemoryJavaCompiler compiler = new InMemoryJavaCompiler();
    CompilerResult result = compiler.compile("StringSorterByText", source);

    // Load
    Map<String, Class<?>> classes = result.loadClassMap();

    // Execute (using Interface)
    StringSorter sorter = (StringSorter)classes.get("StringSorterByText").newInstance();
    List<String> notSorted = Arrays.asList("b", "c", "a");
    System.out.println(notSorted);
	sorter.sort(notSorted);
	System.out.println(notSorted);
    
#### Taking care of packages
 
Your class hierarchy will probably be in a package hierarchy, say `mycompany.myartifact.mycomponent`, then the previous example must be edited to look like this:
    
**Interface**

    package mycompany.myartifact.mycomponent;
    
    public interface StringSorter {
        void sort(List<String> strings);
    }
    
**Subject code**

    String source =
        "package mycompany.myartifact.mycomponent;\n"+    
        "import java.util.Collections;\n"+
        "import java.util.List;\n"+
        "public class StringSorterByText implements StringSorter {\n"+
            "@Override\n"+
            "public void sort(List<String> strings) {\n"+
                "Collections.sort(strings);\n"+
            "}\n"+
        "}";
        
**Usage**

    // Compile
    InMemoryJavaCompiler compiler = new InMemoryJavaCompiler();
    CompilerResult result = compiler.compile("StringSorterByText", source);

    // Load
    Map<String, Class<?>> classes = result.loadClassMap();

    // Execute
    StringSorter sorter = (StringSorter)classes.get("mycompany.myartifact.mycomponent.StringSorterByText").newInstance();
    List<String> notSorted = Arrays.asList("b", "c", "a");
    System.out.println(notSorted);
	sorter.sort(notSorted);
	System.out.println(notSorted);
