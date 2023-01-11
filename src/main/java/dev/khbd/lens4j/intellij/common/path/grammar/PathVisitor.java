package dev.khbd.lens4j.intellij.common.path.grammar;

/**
 * @author Sergei_Khadanovich
 */
public interface PathVisitor {

    /**
     * Invoked before traversing path.
     */
    default void start() {
    }

    /**
     * Visit path part.
     */
    default void visit(PathPart part) {
        if (part instanceof Point p) {
            visitPoint(p);
        } else if (part instanceof Property p) {
            visitProperty(p);
        } else if (part instanceof Method m) {
            visitMethod(m);
        }
    }

    /**
     * Visit point.
     */
    default void visitPoint(Point point) {
    }

    /**
     * Visit property.
     */
    default void visitProperty(Property property) {
    }

    /**
     * Visit method.
     */
    default void visitMethod(Method method) {
    }

    /**
     * Invoked after path visiting.
     */
    default void finish() {
    }
}