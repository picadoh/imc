### In-Memory Java Compiler
The in-memory compiler allows you to compile java source to in-memory bytecode.

#### Sample usage

    String source =
        "public class HelloWorld {\n"+
            "public static void main(String[] args) {\n"+
                "System.out.println();\n"+
            "}\n"+
        "}";

    // **** COMPILE
    InMemoryCompiler compiler = new InMemoryCompiler();
    CompilationPackage pkg = compiler.singleCompile("HelloWorld", source);

    // **** LOAD
    CompilationPackageLoader loader = new CompilationPackageLoader();
    Map<String, Class<?>> classes = loader.loadAsMap(pkg);

    // **** EXECUTE (using reflection)
    classes.get("HelloWorld").getMethod("main", String[].class).invoke(null, (Object) null);

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

    // **** COMPILE
    InMemoryCompiler compiler = new InMemoryCompiler();
    CompilationPackage pkg = compiler.singleCompile("StringSorterByText", source);

    // **** LOAD
    CompilationPackageLoader loader = new CompilationPackageLoader();
    Map<String, Class<?>> classes = loader.loadAsMap(pkg);

    // **** EXECUTE
    StringSorter sorter = (StringSorter)classes.get("StringSorterByText").newInstance();
    List<String> notSorted = Arrays.asList("b", "c", "a");
    System.out.println(notSorted);
	sorter.sort(notSorted);
	System.out.println(notSorted);
    
#### Taking care of packages
    
    Your class hierarchy will probably be in a package hierarchy, say `mycompany.myartifact.mycomponent`, then the previous example must be edited to look like this:
    
** Interface **

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

    // **** COMPILE
    InMemoryCompiler compiler = new InMemoryCompiler();
    CompilationPackage pkg = compiler.singleCompile("StringSorterByText", source);

    // **** LOAD
    CompilationPackageLoader loader = new CompilationPackageLoader();
    Map<String, Class<?>> classes = loader.loadAsMap(pkg);

    // **** EXECUTE
    StringSorter sorter = (StringSorter)classes.get("mycompany.myartifact.mycomponent.StringSorterByText").newInstance();
    List<String> notSorted = Arrays.asList("b", "c", "a");
    System.out.println(notSorted);
	sorter.sort(notSorted);
	System.out.println(notSorted);
