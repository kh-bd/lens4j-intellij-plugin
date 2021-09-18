package dev.khbd.lens4j.intellij.reference;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.PathParser;
import dev.khbd.lens4j.intellij.common.PsiPath;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.psiElement(PsiLiteralValue.class)
                        .insideAnnotationParam(
                                StandardPatterns.string().equalTo(Lens.class.getCanonicalName()),
                                "path"
                        ),
                new LensPathPsiReferenceProvider()
        );
    }

    private static class LensPathPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference[] getReferencesByElement(PsiElement element,
                                                     ProcessingContext context) {
            // TODO fix it
            PsiClass psiClass = com.intellij.psi.util.PsiUtil.getTopLevelClass(element);

            if (Objects.isNull(psiClass) || psiClass.isInterface()) {
                return PsiReference.EMPTY_ARRAY;
            }

            return LensPsiUtil.getStringValue((PsiLiteralValue) element)
                    .filter(path -> !path.isBlank())
                    .map(getReferencesByPathF(psiClass, element))
                    .orElse(PsiReference.EMPTY_ARRAY);
        }

        private Function<String, PsiReference[]> getReferencesByPathF(PsiClass rootClass,
                                                                      PsiElement literalValue) {
            return pathStr -> {
                PsiPath path = new PathParser().psiParse(pathStr, rootClass);
                return path.stream()
                        .map(pathElement -> new LensPathPsiReference(literalValue, pathElement))
                        .toArray(PsiReference[]::new);
            };
        }
    }

}
