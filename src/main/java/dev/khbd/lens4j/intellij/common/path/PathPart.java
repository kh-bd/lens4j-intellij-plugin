package dev.khbd.lens4j.intellij.common.path;

import com.intellij.openapi.util.TextRange;

/**
 * Base interface for all path parts.
 *
 * @author Sergei_Khadanovich
 */
public interface PathPart {

    /**
     * Visit part with specified visitor.
     *
     * @param visitor visitor
     */
    void visit(PathVisitor visitor);

    /**
     * Get part kind.
     *
     * @return part kind
     */
    PathPartKind getKind();

    /**
     * Return text range of current path inside original path.
     *
     * @return text range
     */
    TextRange getTextRange();

    /**
     * Compare kinds of current part and supplied one.
     *
     * @param other part to compare
     * @return {@literal true} if both parts have the same kind and {@literal false} otherwise
     */
    default boolean hasTheSameKindWith(PathPart other) {
        return getKind() == other.getKind();
    }
}
