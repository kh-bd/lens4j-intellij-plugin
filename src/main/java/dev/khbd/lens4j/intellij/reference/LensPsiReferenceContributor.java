package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;

/**
 * @author Sergei_Khadanovich
 */
public class LensPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(LensPsiUtil.LENS_PATH_PATTERN,
                new LensPathPsiReferenceProvider()
        );
        registrar.registerReferenceProvider(LensPsiUtil.LENS_FACTORY_NAME_PATTERN,
                new LensFactoryClassPsiReferenceProvider()
        );
    }
}
