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
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiModifier;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.intellij.Lens4jBundle;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathValidityInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkClass(PsiClass psiClass, InspectionManager manager, boolean isOnTheFly) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens) || psiClass.isInterface() || isInnerClass(psiClass)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        List<PsiAnnotation> lenses = findLensAnnotations(genLens);
        return checkLensAnnotations(psiClass, lenses, manager, isOnTheFly)
                .toArray(ProblemDescriptor[]::new);
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
        Object value = literalValue.getValue();
        if (Objects.isNull(value)) {
            return List.of();
        }

        String pathStr = (String) value;

        if (pathStr.isBlank()) {
            return List.of(pathIsBlankProblem(manager, literalValue, isOnTheFly));
        }

        return checkLensPath(pathStr, psiClass, literalValue, manager, isOnTheFly);
    }

    private List<ProblemDescriptor> checkLensPath(String pathStr,
                                                  PsiClass psiClass,
                                                  PsiLiteralValue literalValue,
                                                  InspectionManager manager,
                                                  boolean isOnTheFly) {
        PsiClass currentPsiClass = psiClass;

        Path path = new PathParser().parse(pathStr);

        for (PathPart part : path) {
            PsiField property = findProperty(currentPsiClass, part.getProperty());
            if (Objects.isNull(property)) {
                ProblemDescriptor problem = propertyNotFoundProblem(literalValue, manager, isOnTheFly, currentPsiClass, part);
                return List.of(problem);
            }

            PsiClassType classType = (PsiClassType) property.getType();
            PsiClass resolvedPsiClass = classType.resolve();

            if (Objects.isNull(resolvedPsiClass)) {
                break;
            }

            currentPsiClass = resolvedPsiClass;
        }

        return List.of();
    }

    private ProblemDescriptor propertyNotFoundProblem(PsiLiteralValue literalValue,
                                                      InspectionManager manager,
                                                      boolean isOnTheFly,
                                                      PsiClass psiClass,
                                                      PathPart part) {
        TextRange range = new TextRange(part.getStart() + 1, part.getEnd() + 2);
        return manager.createProblemDescriptor(
                literalValue,
                range,
                Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.property.not.exist",
                        part.getProperty(), psiClass.getName()
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

    private PsiField findProperty(PsiClass psiClass, String propertyName) {
        PsiField[] fields = psiClass.getAllFields();
        return Arrays.stream(fields)
                .filter(field -> !field.getModifierList().hasExplicitModifier(PsiModifier.STATIC))
                .filter(field -> field.getName().equals(propertyName))
                .findFirst()
                .orElse(null);
    }

    private boolean isInnerClass(PsiClass psiClass) {
        PsiClass containingClass = psiClass.getContainingClass();
        return Objects.nonNull(containingClass);
    }
}
