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
import dev.khbd.lens4j.intellij.common.PsiUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Inspection to verify {@link GenLenses} annotation locates in right place.
 *
 * @author Sergei_Khadanovich
 */
public class GenLensesAtWrongPlaceInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        if (psiClass.isInterface()) {
            return new ProblemDescriptor[]{genLensOnInterface(genLens, manager, isOnTheFly)};
        }

        if (PsiUtil.isNested(psiClass)) {
            return new ProblemDescriptor[]{genLensOnNestedClass(genLens, manager, isOnTheFly)};
        }

        return ProblemDescriptor.EMPTY_ARRAY;
    }

    private ProblemDescriptor genLensOnNestedClass(PsiAnnotation genLens,
                                                   InspectionManager manager,
                                                   boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                genLens,
                Lens4jBundle.getMessage("inspection.gen.lenses.on.nested.class"),
                new RemoveGenLensLocalQuickFix("inspection.gen.lenses.on.nested.class.remove.annotation"),
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly
        );
    }

    private ProblemDescriptor genLensOnInterface(PsiAnnotation genLens,
                                                 InspectionManager manager,
                                                 boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                genLens,
                Lens4jBundle.getMessage("inspection.gen.lenses.on.interface"),
                new RemoveGenLensLocalQuickFix("inspection.gen.lenses.on.interface.remove.annotation"),
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly
        );
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static class RemoveGenLensLocalQuickFix implements LocalQuickFix {

        private final String key;

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
