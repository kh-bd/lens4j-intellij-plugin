package dev.khbd.lens4j.intellij.reference.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;

/**
 * @author Sergei_Khadanovich
 */
public class FactoryClassPsiReference extends PsiReferenceBase<PsiElement> {

    private final PsiClass enclosingClass;
    private final String factoryName;

    public FactoryClassPsiReference(PsiClass enclosingClass,
                                    PsiElement factoryLiteral,
                                    String factoryName) {
        super(factoryLiteral);
        this.enclosingClass = enclosingClass;
        this.factoryName = factoryName;
    }

    @Override
    public PsiElement resolve() {
        return LensPsiUtil.findTheSamePackageClass(enclosingClass, factoryName)
                .orElse(null);
    }
}
