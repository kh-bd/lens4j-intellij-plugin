package dev.khbd.lens4j.intellij.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.common.path.grammar.Path;
import dev.khbd.lens4j.intellij.common.path.grammar.PathParser;
import dev.khbd.lens4j.intellij.reference.psi.LensPathPsiReference;

/**
 * @author Sergei_Khadanovich
 */
class LensPathPsiReferenceProvider extends AbstractNotBlankStringLiteralReferenceProvider {

    @Override
    protected PsiReference[] getReferences(PsiClass enclosingClass,
                                           PsiElement originalElement,
                                           String pathStr) {
        Path path = PathParser.getInstance().parse(pathStr);
        return path.correctPrefix()
                .subPaths()
                .stream()
                .map(subPath -> new LensPathPsiReference(originalElement, subPath, enclosingClass))
                .toArray(PsiReference[]::new);
    }
}
