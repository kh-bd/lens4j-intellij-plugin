package dev.khbd.lens4j.intellij.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.grammar.Method;
import dev.khbd.lens4j.intellij.common.path.grammar.Path;
import dev.khbd.lens4j.intellij.common.path.grammar.PathParser;
import dev.khbd.lens4j.intellij.common.path.grammar.PathVisitor;
import dev.khbd.lens4j.intellij.common.path.grammar.Point;
import dev.khbd.lens4j.intellij.common.path.grammar.Property;
import lombok.RequiredArgsConstructor;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathAnnotator extends AbstractNotBlankStringLiteralAnnotator {

    @Override
    protected boolean isLiteralMatches(PsiElement element) {
        return LensPsiUtil.LENS_PATH_PATTERN.accepts(element);
    }

    @Override
    protected void annotateStringLiteral(String literalValue,
                                         TextRange originalTextRange,
                                         AnnotationHolder holder) {
        PathParser parser = PathParser.getInstance();
        Path path = parser.parse(literalValue).correctPrefix();
        path.visit(new PathPartAnnotatorVisitor(originalTextRange, holder));
    }

    @RequiredArgsConstructor
    private static final class PathPartAnnotatorVisitor implements PathVisitor {

        private final TextRange originalElementRange;
        private final AnnotationHolder holder;

        @Override
        public void visitProperty(Property property) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(property.textRange()))
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                    .create();
        }

        @Override
        public void visitPoint(Point point) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(point.textRange()))
                    .textAttributes(DefaultLanguageHighlighterColors.DOT)
                    .create();
        }

        @Override
        public void visitMethod(Method method) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(method.methodNameTextRange()))
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_METHOD)
                    .create();
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(method.methodParenthesesTextRange()))
                    .textAttributes(DefaultLanguageHighlighterColors.PARENTHESES)
                    .create();
        }

        private TextRange toGlobalTextRange(TextRange textRange) {
            return textRange.shiftRight(originalElementRange.getStartOffset() + 1);
        }
    }
}
