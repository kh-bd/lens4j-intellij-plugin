package dev.khbd.lens4j.intellij.common.path;

import com.google.common.base.CaseFormat;
import dev.khbd.lens4j.intellij.common.path.grammar.Method;
import dev.khbd.lens4j.intellij.common.path.grammar.Path;
import dev.khbd.lens4j.intellij.common.path.grammar.PathParser;
import dev.khbd.lens4j.intellij.common.path.grammar.PathVisitor;
import dev.khbd.lens4j.intellij.common.path.grammar.Property;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class DefaultLensNameDeriver implements PathVisitor {

    private final boolean read;
    private final StringBuilder builder = new StringBuilder();

    private DefaultLensNameDeriver(boolean read) {
        this.read = read;
    }

    @Override
    public void visitProperty(Property property) {
        visitNamed(property.name());
    }

    @Override
    public void visitMethod(Method method) {
        visitNamed(method.name());
    }

    private void visitNamed(String name) {
        if (builder.length() != 0) {
            builder.append("_");
        }
        builder.append(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name));
    }

    @Override
    public void finish() {
        builder.append("_");
        if (read) {
            builder.append("READ_LENS");
        } else {
            builder.append("READ_WRITE_LENS");
        }
    }

    private String getLensName() {
        return builder.toString();
    }

    /**
     * Derive default lens name by path.
     *
     * <p>Note: Path must have correct structure.
     *
     * @param path lens path
     * @param read lens type
     * @return derived lens name
     * @see Path#isStructureCorrect()
     * @see Path#correctPrefix()
     */
    public static String derive(Path path, boolean read) {
        DefaultLensNameDeriver resolver = new DefaultLensNameDeriver(read);
        path.visit(resolver);
        return resolver.getLensName();
    }

    /**
     * Derive default lens name by path.
     *
     * @param pathStr path as string
     * @param read    lens type
     * @return derived lens name or empty if path has incorrect structure
     */
    public static Optional<String> derive(String pathStr, boolean read) {
        Path path = PathParser.getInstance().parse(pathStr);
        if (path.isStructureCorrect()) {
            return Optional.of(derive(path, read));
        }
        return Optional.empty();
    }
}