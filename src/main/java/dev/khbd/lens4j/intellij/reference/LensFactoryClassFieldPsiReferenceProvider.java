package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.reference.psi.FactoryClassFieldPsiReference;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryClassFieldPsiReferenceProvider extends AbstractNotBlankStringLiteralReferenceProvider {

    @Override
    protected PsiReference[] getReferences(PsiClass enclosingClass,
                                           PsiElement originalElement,
                                           String lensName) {
        return new PsiReference[]{new FactoryClassFieldPsiReference(enclosingClass, originalElement, lensName)};
    }
}
