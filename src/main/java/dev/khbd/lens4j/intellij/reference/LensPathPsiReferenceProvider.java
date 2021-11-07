package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathParser;
import dev.khbd.lens4j.intellij.common.path.PathService;
import dev.khbd.lens4j.intellij.reference.psi.LensPathPsiReference;

/**
 * @author Sergei_Khadanovich
 */
class LensPathPsiReferenceProvider extends AbstractNotBlankStringLiteralReferenceProvider {

    @Override
    protected PsiReference[] getReferences(PsiClass enclosingClass,
                                           PsiElement originalElement,
                                           String pathStr) {
        PathParser parser = PathParser.getInstance();

        Path path = parser.parse(pathStr);

        return PathService.getInstance()
                .getCorrectPathPrefixSubPaths(path)
                .stream()
                .map(subPath -> new LensPathPsiReference(originalElement, subPath, enclosingClass))
                .toArray(PsiReference[]::new);
    }
}
