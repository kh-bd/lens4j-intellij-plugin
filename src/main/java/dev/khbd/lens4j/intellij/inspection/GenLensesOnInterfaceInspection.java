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
 * Inspection to verify {@link GenLenses} annotation is not present on interfaces.
 *
 * @author Sergei_Khadanovich
 */
public class GenLensesOnInterfaceInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String PROBLEM_KEY = "inspection.gen.lenses.on.interface";
    private static final String FIX_KEY = "inspection.gen.lenses.on.interface.remove.annotation";

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        if (psiClass.isInterface()) {
            return new ProblemDescriptor[]{genLensOnInterface(genLens, manager, isOnTheFly)};
        }

        return ProblemDescriptor.EMPTY_ARRAY;
    }

    private ProblemDescriptor genLensOnInterface(PsiAnnotation genLens,
                                                 InspectionManager manager,
                                                 boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                genLens,
                Lens4jBundle.getMessage(PROBLEM_KEY),
                new RemoveGenLensLocalQuickFix(FIX_KEY),
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly
        );
    }

}
