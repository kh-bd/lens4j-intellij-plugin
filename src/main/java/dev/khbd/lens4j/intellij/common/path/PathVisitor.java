package dev.khbd.lens4j.intellij.common.path;

/**
 * Path visitor.
 *
 * @author Sergei_Khadanovich
 */
interface PathVisitor {

    default void start() {
    }

    default void visitPoint(Point point) {
    }

    default void visitProperty(Property property) {
    }

    default void finish() {
    }
}
