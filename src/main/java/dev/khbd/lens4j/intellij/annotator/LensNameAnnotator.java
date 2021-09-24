package dev.khbd.lens4j.intellij.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;

/**
 * @author Sergei_Khadanovich
 */
public class LensNameAnnotator extends AbstractNotBlankStringLiteralAnnotator {

    @Override
    protected boolean isLiteralMatches(PsiElement element) {
        return LensPsiUtil.LENS_NAME_PATTERN.accepts(element);
    }

    @Override
    protected void annotateStringLiteral(String literalValue,
                                         TextRange originalTextRange,
                                         AnnotationHolder holder) {
        TextRange range = new TextRange(originalTextRange.getStartOffset() + 1, originalTextRange.getEndOffset() - 1);
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range)
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .create();
    }
}
