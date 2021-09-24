package dev.khbd.lens4j.intellij.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;

/**
 * @author Sergei_Khadanovich
 */
public abstract class AbstractNotBlankStringLiteralAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement element, AnnotationHolder holder) {
        if (!isLiteralMatches(element)) {
            return;
        }

        PsiLiteralValue literalValue = (PsiLiteralValue) element;
        LensPsiUtil.getStringValue(literalValue)
                .ifPresent(str -> {
                    if (str.isBlank()) {
                        return;
                    }
                    annotateStringLiteral(str, element.getTextRange(), holder);
                });
    }

    protected abstract boolean isLiteralMatches(PsiElement element);

    protected abstract void annotateStringLiteral(String literalValue,
                                                  TextRange originalTextRange,
                                                  AnnotationHolder holder);
}
