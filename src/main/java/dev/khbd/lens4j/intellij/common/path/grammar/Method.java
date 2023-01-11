package dev.khbd.lens4j.intellij.common.path.grammar;

import com.intellij.openapi.util.TextRange;

/**
 * @author Sergei_Khadanovich
 */
public record Method(String name, int start) implements PathPart {

    @Override
    public TextRange textRange() {
        return methodNameTextRange();
    }

    /**
     * Method name text range.
     *
     * @return text range
     */
    public TextRange methodNameTextRange() {
        return new TextRange(start, start + name.length());
    }

    /**
     * Parentheses text range.
     *
     * @return text range
     */
    public TextRange methodParenthesesTextRange() {
        int methodNameStop = start + name.length();
        return new TextRange(methodNameStop, methodNameStop + 2);
    }
}
