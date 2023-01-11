package dev.khbd.lens4j.intellij.reference.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import dev.khbd.lens4j.intellij.common.path.PsiMemberResolver;
import dev.khbd.lens4j.intellij.common.path.grammar.Path;

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
        return path.lastPart().textRange().shiftRight(1);
    }
}
