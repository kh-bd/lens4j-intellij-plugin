package dev.khbd.lens4j.intellij.reference;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.common.path.Path;
import dev.khbd.lens4j.intellij.common.path.PathParser;
import dev.khbd.lens4j.intellij.reference.psi.LensPathPsiReference;

/**
 * @author Sergei_Khadanovich
 */
class LensPathPsiReferenceProvider extends AbstractNotBlankStringLiteralReferenceProvider {

    @Override
    protected PsiReference[] getReferences(PsiClass enclosingClass,
                                           PsiElement originalElement,
                                           String pathStr) {
        PathParser parser = ApplicationManager.getApplication().getService(PathParser.class);

        Path path = parser.parse(pathStr).getCorrectPathPrefix();

        return path.getAllSubPaths().stream()
                .map(subPath -> new LensPathPsiReference(originalElement, subPath, enclosingClass))
                .toArray(PsiReference[]::new);
    }
}
