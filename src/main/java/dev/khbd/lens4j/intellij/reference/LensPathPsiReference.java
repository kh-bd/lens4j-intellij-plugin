package dev.khbd.lens4j.intellij.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import dev.khbd.lens4j.intellij.common.PsiPathElement;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathPsiReference extends PsiReferenceBase<PsiElement> {

    private final PsiPathElement pathElement;

    public LensPathPsiReference(PsiElement element,
                                PsiPathElement pathElement) {
        super(element, new TextRange(pathElement.getStart() + 1, pathElement.getEnd() + 2));
        this.pathElement = pathElement;
    }

    @Override
    public PsiElement resolve() {
        if (pathElement.isFieldResolved()) {
            return pathElement.getField();
        }
        return null;
    }
}
