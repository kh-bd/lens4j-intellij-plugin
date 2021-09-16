package dev.khbd.lens4j.intellij.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.intellij.Lens4jBundle;

import java.util.Objects;

/**
 * Inspection to verify {@link GenLenses} annotation is not present on inner classes.
 *
 * @author Sergei_Khadanovich
 */
public class GenLensesOnInnerClassInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String PROBLEM_KEY = "inspection.gen.lenses.on.inner.class";
    private static final String FIX_KEY = "inspection.gen.lenses.on.inner.class.remove.annotation";

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        if (isInnerClass(psiClass)) {
            return new ProblemDescriptor[]{genLensOnInnerClass(genLens, manager, isOnTheFly)};
        }

        return ProblemDescriptor.EMPTY_ARRAY;
    }

    private boolean isInnerClass(PsiClass psiClass) {
        PsiClass containingClass = psiClass.getContainingClass();
        return Objects.nonNull(containingClass);
    }

    private ProblemDescriptor genLensOnInnerClass(PsiAnnotation genLens,
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
}
