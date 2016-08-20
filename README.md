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
    List<Class<?>> classes = loader.load(pkg);

    // **** EXECUTE (using reflection)
    classes.get(0).getMethod("main", String[].class).invoke(null, (Object) null);

#### Using a known interface

If the subject source code implements a known interface to the application executing the compiler (e.g. present in the classpath), it will be considered during compilation and can be reused to execute the code instead of using reflection, as in the below example.

**Interface**

    public interface StringSorter {
        void sort(List<String> strings);
    }

**Subject code**

    String source =
        "import java.util.Collections;\n"+
        "public class StringSorterByText {\n"+
            "@Override\n"+
            "public void sort(List<Strings> strings) {\n"+
                "Collections.sort(strings);\n"+
            "}\n"+
        "}";

**Usage**

    // **** COMPILE
    InMemoryCompiler compiler = new InMemoryCompiler();
    CompilationPackage pkg = compiler.singleCompile("StringSorterByText", source);

    // **** LOAD
    CompilationPackageLoader loader = new CompilationPackageLoader();
    List<Class<?>> classes = loader.load(pkg);

    // **** EXECUTE
    StringSorter sorter = classes.get(0).newInstance();
    sorter.sort(newArrayList("c","a","b"));
