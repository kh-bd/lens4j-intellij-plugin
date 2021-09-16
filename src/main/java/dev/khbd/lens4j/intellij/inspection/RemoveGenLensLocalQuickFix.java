package dev.khbd.lens4j.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import dev.khbd.lens4j.intellij.Lens4jBundle;

/**
 * Quick fix to remove gen lens annotation.
 *
 * @author Sergei_Khadanovich
 */
class RemoveGenLensLocalQuickFix implements LocalQuickFix {

    private final String key;

    RemoveGenLensLocalQuickFix(String key) {
        this.key = key;
    }

    @Override
    public String getFamilyName() {
        return Lens4jBundle.getMessage(key);
    }

    @Override
    public void applyFix(Project project, ProblemDescriptor descriptor) {
        PsiElement annotation = descriptor.getPsiElement();

        PsiFile file = annotation.getContainingFile();

        WriteCommandAction.runWriteCommandAction(project, null, null, () -> {
            annotation.delete();
            JavaCodeStyleManager.getInstance(project).optimizeImports(file);
            UndoUtil.markPsiFileForUndo(file);
        }, file);
    }
}
