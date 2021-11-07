package dev.khbd.lens4j.intellij.reference.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReferenceBase;
import dev.khbd.lens4j.intellij.common.FactoryNameResolver;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class FactoryClassFieldPsiReference extends PsiReferenceBase<PsiElement> {

    private final PsiClass enclosingClass;
    private final String lensName;

    public FactoryClassFieldPsiReference(PsiClass enclosingClass,
                                         PsiElement lensNameLiteral,
                                         String lensName) {
        super(lensNameLiteral);
        this.enclosingClass = enclosingClass;
        this.lensName = lensName;
    }

    @Override
    public PsiElement resolve() {
        FactoryNameResolver factoryNameResolver = FactoryNameResolver.getInstance();

        return factoryNameResolver.resolveFactoryName(enclosingClass)
                .flatMap(factoryName -> LensPsiUtil.findTheSamePackageClass(enclosingClass, factoryName))
                .flatMap(this::resolveStaticField)
                .orElse(null);
    }

    private Optional<PsiField> resolveStaticField(PsiClass psiClass) {
        return LensPsiUtil.findField(psiClass, lensName, true);
    }
}
