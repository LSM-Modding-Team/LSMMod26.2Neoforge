import com.sun.source.util.JavacTask;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Parse-only syntax check of every .java file under the given folders (default: src/main/java).
 * It does NOT resolve imports or types, so it can never say that the mod compiles; only Gradle can.
 *
 * Run from the project root:   java tools/ParseAll.java [folder ...]
 * Exit code: 0 = no syntax errors, 1 = syntax errors, 2 = no compiler module in this JRE.
 * Note: the sandbox JRE is 21 and the mod targets Java 25; syntax newer than 21 would be a false alarm.
 */
public class ParseAll {
    public static void main(String[] args) throws IOException {
        List<Path> roots = new ArrayList<>();
        for (String arg : args) {
            roots.add(Path.of(arg));
        }
        if (roots.isEmpty()) {
            roots.add(Path.of("src/main/java"));
        }

        List<Path> files = new ArrayList<>();
        for (Path root : roots) {
            try (Stream<Path> walk = Files.walk(root)) {
                walk.filter(p -> p.toString().endsWith(".java")).sorted().forEach(files::add);
            }
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("No jdk.compiler module in this JRE.");
            System.exit(2);
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            JavacTask task = (JavacTask) compiler.getTask(null, fileManager, diagnostics,
                    List.of("-proc:none"), null, fileManager.getJavaFileObjectsFromPaths(files));
            task.parse();
        }

        int errors = 0;
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                errors++;
                System.out.println(d.getSource().getName() + ":" + d.getLineNumber() + ": " + d.getMessage(null));
            }
        }
        System.out.println(files.size() + " files parsed, " + errors + " syntax errors (parse only, NOT a compile).");
        System.exit(errors == 0 ? 0 : 1);
    }
}
