package dev.khbd.lens4j.intellij.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import dev.khbd.lens4j.intellij.common.path.Path;
import dev.khbd.lens4j.intellij.common.path.Property;
import dev.khbd.lens4j.intellij.common.path.PsiFieldResolver;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathPsiReference extends PsiReferenceBase<PsiElement> {

    private final Path path;
    private final PsiClass rootClass;

    public LensPathPsiReference(PsiElement element,
                                Path path,
                                PsiClass rootClass) {
        super(element, getLastPartRange(path));
        this.path = path;
        this.rootClass = rootClass;
    }

    @Override
    public PsiElement resolve() {
        return PsiFieldResolver.resolveField(path, rootClass);
    }

    private static TextRange getLastPartRange(Path path) {
        Property property = (Property) path.getLastPart();
        int start = property.getStart() + 1;
        return new TextRange(start, start + property.getProperty().length());
    }
}
