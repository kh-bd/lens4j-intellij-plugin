package dev.khbd.lens4j.intellij.common.path.grammar;

import com.intellij.openapi.util.TextRange;

/**
 * @author Sergei_Khadanovich
 */
public sealed interface PathPart permits Point, Method, Property {

    /**
     * Get text range associated with path part.
     *
     * @return text range
     */
    TextRange textRange();
}
