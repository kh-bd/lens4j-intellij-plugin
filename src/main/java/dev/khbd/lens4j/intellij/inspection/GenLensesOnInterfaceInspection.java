package dev.khbd.lens4j.intellij.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.intellij.Lens4jBundle;

import java.util.Objects;

/**
 * Inspection to verify {@link GenLenses} annotation is not present on interfaces.
 *
 * @author Sergei_Khadanovich
 */
public class GenLensesOnInterfaceInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String PROBLEM_KEY = "inspection.gen.lenses.on.interface";
    private static final String FIX_KEY = "inspection.gen.lenses.on.interface.remove.annotation";

    private static final ProblemDescriptor[] NO_DESCRIPTORS = new ProblemDescriptor[0];

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens)) {
            return NO_DESCRIPTORS;
        }

        if (psiClass.isInterface()) {
            return new ProblemDescriptor[]{genLensOnInterface(genLens, manager, isOnTheFly)};
        }

        return NO_DESCRIPTORS;
    }

    private ProblemDescriptor genLensOnInterface(PsiAnnotation genLens,
                                                 InspectionManager manager,
                                                 boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                genLens,
                Lens4jBundle.getMessage(PROBLEM_KEY),
                new RemoveGenLensLocalQuickFix(FIX_KEY),
                ProblemHighlightType.ERROR,
                isOnTheFly
        );
    }

    private static class RemoveGenLensLocalQuickFix implements LocalQuickFix {

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
}
