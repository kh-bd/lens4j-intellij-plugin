package dev.khbd.lens4j.intellij.common.path.grammar;

import com.intellij.openapi.util.TextRange;

/**
 * @author Sergei_Khadanovich
 */
public record Point(int position) implements PathPart {

    @Override
    public TextRange textRange() {
        return new TextRange(position, position + 1);
    }
}
