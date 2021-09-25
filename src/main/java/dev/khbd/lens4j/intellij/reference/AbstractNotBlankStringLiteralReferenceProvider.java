package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;

/**
 * @author Sergei_Khadanovich
 */
public abstract class AbstractNotBlankStringLiteralReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference[] getReferencesByElement(PsiElement element, ProcessingContext context) {
        return LensPsiUtil.findFirstEnclosingClass(element)
                .filter(psiClass -> !psiClass.isInterface())
                .flatMap(psiClass ->
                        LensPsiUtil.getStringValue((PsiLiteralValue) element)
                                .filter(path -> !path.isBlank())
                                .map(str -> getReferences(psiClass, element, str))
                ).orElse(PsiReference.EMPTY_ARRAY);
    }

    protected abstract PsiReference[] getReferences(PsiClass psiClass,
                                                    PsiElement originalElement,
                                                    String value);
}
