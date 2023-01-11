package dev.khbd.lens4j.intellij.common.path.grammar;

import com.intellij.openapi.util.TextRange;

/**
 * @author Sergei_Khadanovich
 */
public record Method(String name, int start) implements PathPart {

    @Override
    public TextRange textRange() {
        return new TextRange(start, start + name.length());
    }
}
