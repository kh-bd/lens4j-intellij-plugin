package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.reference.psi.FactoryClassPsiReference;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryClassPsiReferenceProvider extends AbstractNotBlankStringLiteralReferenceProvider {

    @Override
    protected PsiReference[] getReferences(PsiClass psiClass,
                                           PsiElement originalElement,
                                           String factoryName) {
        return new PsiReference[]{new FactoryClassPsiReference(psiClass, originalElement, factoryName)};
    }
}
