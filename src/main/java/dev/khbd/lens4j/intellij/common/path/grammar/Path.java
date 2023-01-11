package dev.khbd.lens4j.intellij.common.path.grammar;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@ToString
@EqualsAndHashCode
public class Path {

    private final List<PathPart> parts;

    Path(List<PathPart> parts) {
        this.parts = parts;
    }

    /**
     * Get last part.
     */
    public PathPart lastPart() {
        if (isEmpty()) {
            throw new RuntimeException("Path is empty");
        }
        return parts.get(parts.size() - 1);
    }

    /**
     * Get path without last part.
     *
     * @return path prefix
     */
    public Path withoutLastPart() {
        if (isEmpty()) {
            throw new RuntimeException("Path is empty");
        }
        PathBuilder builder = Path.builder();
        for (int i = 0; i < parts.size() - 1; i++) {
            builder.withPart(parts.get(i));
        }
        return builder.build();
    }

    /**
     * Is path empty of not.
     */
    public boolean isEmpty() {
        return parts.isEmpty();
    }

    /**
     * Path length.
     *
     * @return path elements count
     */
    public int length() {
        return parts.size();
    }

    /**
     * Visit path with specified visitor.
     *
     * @param visitor visitor.
     */
    public void visit(PathVisitor visitor) {
        visitor.start();
        for (PathPart part : parts) {
            visitor.visit(part);
        }
        visitor.finish();
    }

    /**
     * Check if structure correct or not.
     *
     * @return {@literal true} if path structure is correct and {@literal false} otherwise
     */
    public boolean isStructureCorrect() {
        return correctPrefix().equals(this);
    }

    /**
     * Get correct path prefix.
     *
     * @return correct prefix
     */
    public Path correctPrefix() {
        CorrectPathPrefixCollector collector = new CorrectPathPrefixCollector();
        visit(collector);
        return collector.getPrefix();
    }

    /**
     * Return all sub paths of current path.
     *
     * <p>For example, for path `pr1.pr2.pr3` all sub paths should be:
     * `pr1`, `pr1.pr2`, `pr1.pr2.pr3`
     *
     * <p>Note: this method should be used only on correct path instance.
     * See {@link #correctPrefix()}.
     *
     * @return all sub paths
     */
    public List<Path> subPaths() {
        SubPathsCollector collector = new SubPathsCollector();
        visit(collector);
        return collector.getSubPaths();
    }

    /**
     * Create empty path builder.
     *
     * @return builder
     */
    public static PathBuilder builder() {
        return new PathBuilder();
    }

    /**
     * Create empty path.
     *
     * @return empty path
     */
    public static Path empty() {
        return new Path(List.of());
    }

    private static final class CorrectPathPrefixCollector implements PathVisitor {

        private final PathBuilder builder = Path.builder();
        private boolean fail;

        @Override
        public void visitPoint(Point point) {
            if (fail) {
                return;
            }
            if (builder.isEmpty()) {
                fail();
                return;
            }
            if (isPoint(builder.getLast())) {
                fail();
                return;
            }
            builder.withPart(point);
        }

        @Override
        public void visitProperty(Property property) {
            visitNamed(property);
        }

        @Override
        public void visitMethod(Method method) {
            visitNamed(method);
        }

        private void visitNamed(PathPart part) {
            if (fail) {
                return;
            }
            if (builder.isEmpty()) {
                builder.withPart(part);
                return;
            }
            if (!isPoint(builder.getLast())) {
                fail();
                return;
            }
            builder.withPart(part);
        }

        Path getPrefix() {
            return builder.build();
        }

        private void fail() {
            fail = true;
        }

        private boolean isPoint(PathPart part) {
            return part instanceof Point;
        }
    }

    private static final class SubPathsCollector implements PathVisitor {

        private final List<Path> result = new ArrayList<>();
        private final PathBuilder builder = Path.builder();

        @Override
        public void visitProperty(Property property) {
            visitNamed(property);
        }

        @Override
        public void visitMethod(Method method) {
            visitNamed(method);
        }

        private void visitNamed(PathPart part) {
            builder.withPart(part);
            result.add(builder.build());
        }

        @Override
        public void visitPoint(Point point) {
            builder.withPart(point);
        }

        List<Path> getSubPaths() {
            return result;
        }
    }
}
