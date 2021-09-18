package dev.khbd.lens4j.intellij.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLiteralValue;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.PathParser;
import dev.khbd.lens4j.intellij.common.PsiPath;
import dev.khbd.lens4j.intellij.common.PsiPathElement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathValidityInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        if (psiClass.isInterface() || LensPsiUtil.isNested(psiClass)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        List<PsiAnnotation> lenses = findLensAnnotations(psiClass);

        return checkLensAnnotations(psiClass, lenses, manager, isOnTheFly)
                .toArray(ProblemDescriptor[]::new);
    }

    private List<PsiAnnotation> findLensAnnotations(PsiClass psiClass) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens)) {
            return List.of();
        }

        return findLensAnnotations(genLens);
    }

    private List<PsiAnnotation> findLensAnnotations(PsiAnnotation genLens) {
        PsiAnnotationMemberValue lenses = genLens.findAttributeValue("lenses");

        if (lenses instanceof PsiAnnotation) {
            return List.of((PsiAnnotation) lenses);
        }

        if (lenses instanceof PsiArrayInitializerMemberValue) {
            PsiArrayInitializerMemberValue initializerMemberValue = (PsiArrayInitializerMemberValue) lenses;
            PsiAnnotationMemberValue[] initializers = initializerMemberValue.getInitializers();
            return Arrays.stream(initializers)
                    .map(PsiAnnotation.class::cast)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    private List<ProblemDescriptor> checkLensAnnotations(PsiClass psiClass,
                                                         List<PsiAnnotation> lenses,
                                                         InspectionManager manager,
                                                         boolean isOnTheFly) {
        return lenses.stream()
                .flatMap(lens -> checkLensAnnotation(psiClass, lens, manager, isOnTheFly).stream())
                .collect(Collectors.toList());
    }

    private List<ProblemDescriptor> checkLensAnnotation(PsiClass psiClass,
                                                        PsiAnnotation lens,
                                                        InspectionManager manager,
                                                        boolean isOnTheFly) {
        PsiAnnotationMemberValue pathMember = lens.findAttributeValue("path");
        // Lens#path can be null, if user has not completed typing yet.
        if (Objects.isNull(pathMember)) {
            return List.of();
        }

        PsiLiteralValue literalValue = (PsiLiteralValue) pathMember;

        return LensPsiUtil.getStringValue(literalValue)
                .flatMap(checkLensPathF(manager, psiClass, literalValue, isOnTheFly))
                .stream()
                .collect(Collectors.toList());
    }

    private Function<String, Optional<ProblemDescriptor>> checkLensPathF(InspectionManager manager,
                                                                         PsiClass psiClass,
                                                                         PsiLiteralValue literalValue,
                                                                         boolean isOnTheFly) {
        return pathStr -> {
            if (pathStr.isBlank()) {
                return Optional.of(pathIsBlankProblem(manager, literalValue, isOnTheFly));
            }
            return checkNotBlankPath(manager, psiClass, literalValue, pathStr, isOnTheFly);
        };
    }

    private Optional<ProblemDescriptor> checkNotBlankPath(InspectionManager manager,
                                                          PsiClass psiClass,
                                                          PsiLiteralValue literalValue,
                                                          String pathStr,
                                                          boolean isOnTheFly) {
        PsiPath psiPath = new PathParser().psiParse(pathStr, psiClass);

        return psiPath.findFirstElementWithUnResolvedField()
                .map(e -> propertyNotFoundProblem(literalValue, e, manager, isOnTheFly));
    }

    private ProblemDescriptor propertyNotFoundProblem(PsiLiteralValue literalValue,
                                                      PsiPathElement element,
                                                      InspectionManager manager,
                                                      boolean isOnTheFly) {
        TextRange range = new TextRange(element.getStart() + 1, element.getEnd() + 2);
        return manager.createProblemDescriptor(
                literalValue,
                range,
                Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.property.not.exist",
                        element.getProperty(), element.getPsiClass().getName()
                ),
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly,
                (LocalQuickFix) null
        );
    }

    private ProblemDescriptor pathIsBlankProblem(InspectionManager manager,
                                                 PsiLiteralValue literalValue,
                                                 boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                literalValue,
                Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.blank"),
                (LocalQuickFix) null,
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly
        );
    }
}
