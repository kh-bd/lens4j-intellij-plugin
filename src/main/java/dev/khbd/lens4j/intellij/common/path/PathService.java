package dev.khbd.lens4j.intellij.common.path;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.util.TextRange;
import dev.khbd.lens4j.common.Method;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathPart;
import dev.khbd.lens4j.common.PathPartKind;
import dev.khbd.lens4j.common.PathVisitor;
import dev.khbd.lens4j.common.Point;
import dev.khbd.lens4j.common.Property;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@Service
public final class PathService {

    public static PathService getInstance() {
        return ApplicationManager.getApplication().getService(PathService.class);
    }

    /**
     * Evaluate text range for specified path part.
     *
     * @param part path part
     * @return text range
     */
    public TextRange getTextRange(@NonNull PathPart part) {
        PathPartKind kind = part.getKind();
        if (kind == PathPartKind.POINT) {
            return getPointTextRange((Point) part);
        } else if (kind == PathPartKind.PROPERTY) {
            return getPropertyNameTextRange((Property) part);
        } else if (kind == PathPartKind.METHOD) {
            return getMethodNameTextRange((Method) part);
        }
        throw new IllegalArgumentException("Unsupported path part");
    }

    /**
     * Evaluate text range for specified point.
     *
     * @param point path point
     * @return text range
     */
    public TextRange getPointTextRange(@NonNull Point point) {
        return new TextRange(point.getPosition(), point.getPosition() + 1);
    }

    /**
     * Evaluate text range for specified property.
     *
     * @param property path property
     * @return text range
     */
    public TextRange getPropertyNameTextRange(@NonNull Property property) {
        return new TextRange(property.getStart(), property.getStart() + property.getName().length());
    }

    /**
     * Evaluate text range for specified method.
     *
     * @param method path method
     * @return text range
     */
    public TextRange getMethodNameTextRange(@NonNull Method method) {
        return new TextRange(method.getStart(), method.getStart() + method.getName().length());
    }

    /**
     * Evaluate text range of parentheses for specified method.
     *
     * @param method path method
     * @return parentheses text range
     */
    public TextRange getMethodParenthesesTextRange(@NonNull Method method) {
        int start = method.getStart() + method.getName().length();
        return new TextRange(start, start + 2);
    }

    /**
     * Get correct path prefix.
     *
     * <p>Correct path has formal structure:
     * path = property | property [point property]* [point]
     *
     * @return correct path prefix
     */
    public Path getCorrectPathPrefix(@NonNull Path path) {
        CorrectPathPrefixCollector collector = new CorrectPathPrefixCollector();
        path.visit(collector);
        return collector.getPathPrefix();
    }

    private static final class CorrectPathPrefixCollector implements PathVisitor {

        private final Path result = new Path();
        private boolean fail;

        @Override
        public void visitPoint(Point point) {
            if (fail) {
                return;
            }
            if (result.isEmpty()) {
                fail();
                return;
            }
            if (result.getLastPart().isPoint()) {
                fail();
                return;
            }
            result.addPart(point);
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
            if (result.isEmpty()) {
                result.addPart(part);
                return;
            }
            if (!result.getLastPart().isPoint()) {
                fail();
                return;
            }
            result.addPart(part);
        }

        public Path getPathPrefix() {
            return result;
        }

        private void fail() {
            fail = true;
        }
    }

    /**
     * Check path has correct structure or not.
     *
     * <p>Correct path has formal structure:
     * path = property | property [point property]* [point]
     *
     * @return {@literal true} if path structure is correct and {@literal false} otherwise
     */
    public boolean hasCorrectStructure(@NonNull Path path) {
        return path.equals(getCorrectPathPrefix(path));
    }

    /**
     * Return all sub paths of current path.
     *
     * <p>For example, for path `pr1.pr2.pr3` all sub paths should be:
     * `pr1`, `pr1.pr2`, `pr1.pr2.pr3`
     *
     * <p>Note: this method should be used only on correct path instance.
     * See {@link #getCorrectPathPrefix(Path)}.
     *
     * @return all sub paths
     */
    public List<Path> getSubPaths(@NonNull Path path) {
        SubPathsCollector collector = new SubPathsCollector();
        path.visit(collector);
        return collector.getSubPaths();
    }

    /**
     * Return all sub paths of specified path's correct sub path.
     *
     * @return all sub paths
     */
    public List<Path> getCorrectPathPrefixSubPaths(@NonNull Path path) {
        return getSubPaths(getCorrectPathPrefix(path));
    }

    private static final class SubPathsCollector implements PathVisitor {

        private final List<Path> result = new ArrayList<>();
        private final Path lastPath = new Path();

        @Override
        public void visitProperty(Property property) {
            lastPath.addPart(property);
            result.add(lastPath.copy());
        }

        @Override
        public void visitPoint(Point point) {
            lastPath.addPart(point);
        }

        List<Path> getSubPaths() {
            return result;
        }
    }

}
