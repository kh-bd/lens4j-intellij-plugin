package dev.khbd.lens4j.intellij.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import dev.khbd.lens4j.common.Method;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathParser;
import dev.khbd.lens4j.common.PathVisitor;
import dev.khbd.lens4j.common.Point;
import dev.khbd.lens4j.common.Property;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.PathService;
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
        Path path = PathService.getInstance().getCorrectPathPrefix(parser.parse(literalValue));
        path.visit(new PathPartAnnotatorVisitor(originalTextRange, holder));
    }

    @RequiredArgsConstructor
    private static class PathPartAnnotatorVisitor implements PathVisitor {

        private final TextRange originalElementRange;
        private final AnnotationHolder holder;

        @Override
        public void visitProperty(Property property) {
            PathService pathService = PathService.getInstance();
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(pathService.getPropertyNameTextRange(property)))
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                    .create();
        }

        @Override
        public void visitPoint(Point point) {
            PathService pathService = PathService.getInstance();
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(pathService.getPointTextRange(point)))
                    .textAttributes(DefaultLanguageHighlighterColors.DOT)
                    .create();
        }

        @Override
        public void visitMethod(Method method) {
            PathService pathService = PathService.getInstance();
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(pathService.getMethodNameTextRange(method)))
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_METHOD)
                    .create();
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(toGlobalTextRange(pathService.getMethodParenthesesTextRange(method)))
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_METHOD)
                    .create();
        }

        private TextRange toGlobalTextRange(TextRange textRange) {
            return textRange.shiftRight(originalElementRange.getStartOffset() + 1);
        }
    }
}
