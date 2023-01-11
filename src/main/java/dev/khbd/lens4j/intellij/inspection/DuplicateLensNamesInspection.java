package dev.khbd.lens4j.intellij.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLiteralValue;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.DefaultLensNameDeriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Sergei_Khadanovich
 */
public class DuplicateLensNamesInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        List<PsiAnnotation> lenses = LensPsiUtil.lenses(psiClass);

        if (lenses.isEmpty()) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        Set<String> names = new HashSet<>();
        List<ProblemDescriptor> problems = new ArrayList<>();

        for (PsiAnnotation lens : lenses) {
            PsiAnnotationMemberValue lensNameAttribute = lens.findDeclaredAttributeValue("lensName");
            if (Objects.nonNull(lensNameAttribute)) {
                PsiLiteralValue lensNameLiteral = (PsiLiteralValue) lensNameAttribute;
                String lensName = LensPsiUtil.getStringValue(lensNameLiteral).orElse(null);

                // lensName specified explicitly and contains string literal value
                // else branch means user specified incorrect lensName value,
                // for example, null literal
                if (Objects.nonNull(lensName)) {
                    if (names.contains(lensName)) {
                        ProblemDescriptor problem = manager.createProblemDescriptor(
                                lensNameLiteral,
                                Lens4jBundle.getMessage("inspection.gen.lenses.lens.names.uniqueness.duplicate.name"),
                                (LocalQuickFix) null,
                                ProblemHighlightType.GENERIC_ERROR,
                                isOnTheFly
                        );
                        problems.add(problem);
                    } else {
                        names.add(lensName);
                    }
                }
            } else {
                // user has not specified lensName attribute explicitly, but specified path attribute
                PsiAnnotationMemberValue pathAttribute = lens.findAttributeValue("path");
                if (Objects.nonNull(pathAttribute)) {
                    PsiLiteralValue pathLiteral = (PsiLiteralValue) pathAttribute;
                    String path = LensPsiUtil.getStringValue(pathLiteral).orElse(null);

                    // path was specified explicitly and contains string literal value
                    if (Objects.nonNull(path)) {
                        String lensName = DefaultLensNameDeriver.derive(path, LensPsiUtil.isRead(lens)).orElse(null);
                        if (Objects.nonNull(lensName)) {
                            if (names.contains(lensName)) {
                                ProblemDescriptor problem = manager.createProblemDescriptor(
                                        pathLiteral,
                                        Lens4jBundle.getMessage("inspection.gen.lenses.lens.names.uniqueness.duplicate.path",
                                                lensName
                                        ),
                                        (LocalQuickFix) null,
                                        ProblemHighlightType.GENERIC_ERROR,
                                        isOnTheFly
                                );
                                problems.add(problem);
                            } else {
                                names.add(lensName);
                            }
                        }
                    }
                }
            }
        }

        return problems.toArray(ProblemDescriptor[]::new);
    }

}
