package dev.khbd.lens4j.intellij.intention;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.siyeh.ipp.base.Intention;
import com.siyeh.ipp.base.PsiElementPredicate;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class RegenerateLensFactoryIntention extends Intention {

    @Override
    protected void processIntention(PsiElement element) {
        PsiFile file = element.getContainingFile();
        CompilerManager manager = CompilerManager.getInstance(element.getProject());
        manager.compile(new VirtualFile[]{file.getVirtualFile()}, null);
    }

    @Override
    @NotNull
    protected PsiElementPredicate getElementPredicate() {
        return element -> {
            if (!(element instanceof PsiClass)) {
                return false;
            }
            PsiClass psiClass = (PsiClass) element;
            PsiAnnotation genLensAnnotation = psiClass.getAnnotation(GenLenses.class.getCanonicalName());
            return Objects.nonNull(genLensAnnotation);
        };
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return Lens4jBundle.getMessage("intentions");
    }

    @Override
    @NotNull
    public String getText() {
        return Lens4jBundle.getMessage("intention.regenerate.lens.factory");
    }

}
