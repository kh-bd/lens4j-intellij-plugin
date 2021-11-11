package dev.khbd.lens4j.intellij.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import dev.khbd.lens4j.common.Method;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathParser;
import dev.khbd.lens4j.common.PathPart;
import dev.khbd.lens4j.common.Property;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.PathService;
import dev.khbd.lens4j.intellij.common.path.PsiMemberResolver;

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
        if (psiClass.isInterface()) {
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
                .flatMap(checkLensPathF(manager, psiClass, lens, literalValue, isOnTheFly))
                .stream()
                .collect(Collectors.toList());
    }

    private Function<String, Optional<ProblemDescriptor>> checkLensPathF(InspectionManager manager,
                                                                         PsiClass psiClass,
                                                                         PsiAnnotation lens,
                                                                         PsiLiteralValue literalValue,
                                                                         boolean isOnTheFly) {
        return pathStr -> {
            if (pathStr.isBlank()) {
                return Optional.of(pathIsBlankProblem(manager, literalValue, isOnTheFly));
            }
            return checkNotBlankPath(manager, psiClass, lens, literalValue, pathStr, isOnTheFly);
        };
    }

    private Optional<ProblemDescriptor> checkNotBlankPath(InspectionManager manager,
                                                          PsiClass psiClass,
                                                          PsiAnnotation lens,
                                                          PsiLiteralValue literalValue,
                                                          String pathStr,
                                                          boolean isOnTheFly) {
        PathParser parser = PathParser.getInstance();
        Path path = PathService.getInstance().getCorrectPathPrefix(parser.parse(pathStr));

        PsiMemberResolver resolver = new PsiMemberResolver(psiClass);
        path.visit(resolver);

        if (resolver.isResolved()) {
            return checkResolvedPath(lens, path, literalValue, manager, isOnTheFly);
        }

        return deriveNotFoundProblem(resolver, literalValue, manager, isOnTheFly);
    }

    private Optional<ProblemDescriptor> checkResolvedPath(PsiAnnotation lens,
                                                          Path path,
                                                          PsiLiteralValue literalValue,
                                                          InspectionManager manager,
                                                          boolean isOnTheFly) {
        if (lensIsWrite(lens)) {
            PathPart lastPart = path.getLastPart();
            if (lastPart.isMethod()) {
                return Optional.of(methodNotAllowedAtWritePosition(literalValue, (Method) lastPart, manager, isOnTheFly));
            }
        }
        return Optional.empty();
    }

    private boolean lensIsWrite(PsiAnnotation lens) {
        PsiAnnotationMemberValue typeMember = lens.findAttributeValue("type");
        if (!(typeMember instanceof PsiReferenceExpression)) {
            return false;
        }
        PsiReferenceExpression ref = (PsiReferenceExpression) typeMember;
        PsiField field = (PsiField) ref.resolve();
        if (Objects.isNull(field)) {
            return false;
        }
        return "READ_WRITE".equals(field.getName());
    }

    private ProblemDescriptor methodNotAllowedAtWritePosition(PsiLiteralValue literalValue,
                                                              Method method,
                                                              InspectionManager manager,
                                                              boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                literalValue,
                PathService.getInstance().getMethodNameTextRange(method).shiftRight(1),
                Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.method.at.write.position"),
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly,
                (LocalQuickFix) null
        );
    }

    private Optional<ProblemDescriptor> deriveNotFoundProblem(PsiMemberResolver resolver,
                                                              PsiLiteralValue literalValue,
                                                              InspectionManager manager,
                                                              boolean isOnTheFly) {
        PathPart part = resolver.getNonResolvedPart();
        if (part.isProperty()) {
            return Optional.of(propertyNotFoundProblem(literalValue, (Property) part,
                    resolver.getLastResolvedType(), manager,
                    isOnTheFly));
        }
        if (part.isMethod()) {
            return Optional.of(methodNotFoundProblem(literalValue, (Method) part,
                    resolver.getLastResolvedType(), manager,
                    isOnTheFly));
        }
        return Optional.empty();
    }

    private ProblemDescriptor propertyNotFoundProblem(PsiLiteralValue literalValue,
                                                      Property property,
                                                      PsiType type,
                                                      InspectionManager manager,
                                                      boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                literalValue,
                PathService.getInstance().getPropertyNameTextRange(property).shiftRight(1),
                Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.property.not.exist",
                        property.getName(), type.getPresentableText()
                ),
                ProblemHighlightType.GENERIC_ERROR,
                isOnTheFly,
                (LocalQuickFix) null
        );
    }

    private ProblemDescriptor methodNotFoundProblem(PsiLiteralValue literalValue,
                                                    Method method,
                                                    PsiType type,
                                                    InspectionManager manager,
                                                    boolean isOnTheFly) {
        return manager.createProblemDescriptor(
                literalValue,
                PathService.getInstance().getMethodNameTextRange(method).shiftRight(1),
                Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.method.not.exist",
                        method.getName(), type.getPresentableText()
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
