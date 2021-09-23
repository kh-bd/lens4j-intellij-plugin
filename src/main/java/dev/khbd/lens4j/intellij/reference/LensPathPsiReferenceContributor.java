package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.Path;
import dev.khbd.lens4j.intellij.common.path.PathParser;

import java.util.function.Function;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(LensPsiUtil.LENS_PATH_PATTERN, new LensPathPsiReferenceProvider());
    }

    private static class LensPathPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference[] getReferencesByElement(PsiElement element,
                                                     ProcessingContext context) {

            return LensPsiUtil.findFirstEnclosingClass(element)
                    .filter(psiClass -> !psiClass.isInterface())
                    .flatMap(psiClass ->
                            LensPsiUtil.getStringValue((PsiLiteralValue) element)
                                    .filter(path -> !path.isBlank())
                                    .map(getReferencesByPathF(psiClass, element))
                    ).orElse(PsiReference.EMPTY_ARRAY);
        }

        private Function<String, PsiReference[]> getReferencesByPathF(PsiClass rootClass,
                                                                      PsiElement literalValue) {
            return pathStr -> {
                Path path = new PathParser().parse(pathStr).getCorrectPathPrefix();

                return path.getAllSubPaths().stream()
                        .map(subPath -> new LensPathPsiReference(literalValue, subPath, rootClass))
                        .toArray(PsiReference[]::new);
            };
        }
    }

}
