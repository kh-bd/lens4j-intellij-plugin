package dev.khbd.lens4j.intellij.reference.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.intellij.common.path.PathService;
import dev.khbd.lens4j.intellij.common.path.PsiMemberResolver;

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
        return PsiMemberResolver.resolveMember(path, rootClass);
    }

    private static TextRange getLastPartRange(Path path) {
        return PathService.getInstance().getTextRange(path.getLastPart()).shiftRight(1);
    }
}
