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
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.PsiMemberResolver;
import dev.khbd.lens4j.intellij.common.path.grammar.Method;
import dev.khbd.lens4j.intellij.common.path.grammar.Path;
import dev.khbd.lens4j.intellij.common.path.grammar.PathParser;
import dev.khbd.lens4j.intellij.common.path.grammar.PathPart;
import dev.khbd.lens4j.intellij.common.path.grammar.Property;
import lombok.RequiredArgsConstructor;

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
        List<PsiAnnotation> lenses = LensPsiUtil.lenses(psiClass);

        if (lenses.isEmpty()) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }


        return checkLensAnnotations(psiClass, lenses, manager, isOnTheFly)
                .toArray(ProblemDescriptor[]::new);
    }

    private List<ProblemDescriptor> checkLensAnnotations(PsiClass psiClass,
                                                         List<PsiAnnotation> lenses,
                                                         InspectionManager manager,
                                                         boolean isOnTheFly) {
        LensInspector inspector = new LensInspector(psiClass, manager, isOnTheFly);
        return lenses.stream()
                .flatMap(lens -> inspector.inspect(lens).stream())
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    private static class LensInspector {
        final PsiClass psiClass;
        final InspectionManager manager;
        final boolean isOnTheFly;

        List<ProblemDescriptor> inspect(PsiAnnotation lens) {
            PsiAnnotationMemberValue pathMember = lens.findAttributeValue("path");
            // Lens#path can be null, if user has not completed typing yet.
            if (Objects.isNull(pathMember)) {
                return List.of();
            }

            PsiLiteralValue literalValue = (PsiLiteralValue) pathMember;

            return LensPsiUtil.getStringValue(literalValue)
                    .flatMap(checkLensPathF(lens, literalValue))
                    .stream()
                    .collect(Collectors.toList());
        }

        Function<String, Optional<ProblemDescriptor>> checkLensPathF(PsiAnnotation lens,
                                                                     PsiLiteralValue literalValue) {
            return pathStr -> {
                if (pathStr.isBlank()) {
                    return Optional.of(pathIsBlankProblem(literalValue));
                }
                return inspectNotBlankPath(lens, literalValue, pathStr);
            };
        }

        ProblemDescriptor pathIsBlankProblem(PsiLiteralValue literalValue) {
            return manager.createProblemDescriptor(
                    literalValue,
                    Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.blank"),
                    (LocalQuickFix) null,
                    ProblemHighlightType.GENERIC_ERROR,
                    isOnTheFly
            );
        }

        Optional<ProblemDescriptor> inspectNotBlankPath(PsiAnnotation lens,
                                                        PsiLiteralValue literalValue,
                                                        String pathStr) {
            Path path = PathParser.getInstance().parse(pathStr).correctPrefix();

            PsiMemberResolver resolver = new PsiMemberResolver(psiClass);
            path.visit(resolver);

            if (resolver.isResolved()) {
                return checkLastPathPart(lens, path, literalValue, resolver);
            }

            return deriveNotFoundProblem(resolver, literalValue);
        }

        Optional<ProblemDescriptor> checkLastPathPart(PsiAnnotation lens,
                                                      Path path,
                                                      PsiLiteralValue literalValue,
                                                      PsiMemberResolver resolver) {
            if (LensPsiUtil.isWrite(lens)) {
                PathPart lastPart = path.lastPart();
                if (lastPart instanceof Method method) {
                    return Optional.of(methodAtWritePositionProblem(literalValue, method));
                }
                if (lastPart instanceof Property property) {
                    return checkLastProperty(literalValue, property, resolver);
                }
            }
            return Optional.empty();
        }

        Optional<ProblemDescriptor> checkLastProperty(PsiLiteralValue literalValue,
                                                      Property property,
                                                      PsiMemberResolver resolver) {
            PsiMember field = resolver.getResolvedMember();
            if (field.getModifierList().hasExplicitModifier(PsiModifier.FINAL)) {
                return Optional.of(finalPropertyAtWritePositionProblem(literalValue, property));
            }
            return Optional.empty();
        }

        ProblemDescriptor finalPropertyAtWritePositionProblem(PsiLiteralValue literalValue,
                                                              Property property) {
            return errorProblem(literalValue, property,
                    Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.final.property.at.write.position"));
        }

        Optional<ProblemDescriptor> deriveNotFoundProblem(PsiMemberResolver resolver,
                                                          PsiLiteralValue literalValue) {
            PathPart part = resolver.getNonResolvedPart();
            if (part instanceof Property property) {
                return Optional.of(propertyNotFoundProblem(literalValue, property,
                        resolver.getLastResolvedType()));
            }
            if (part instanceof Method method) {
                return Optional.of(methodNotFoundProblem(literalValue, method,
                        resolver.getLastResolvedType()));
            }
            return Optional.empty();
        }

        ProblemDescriptor propertyNotFoundProblem(PsiLiteralValue literalValue,
                                                  Property property,
                                                  PsiType type) {
            String message = Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.property.not.exist",
                    property.name(), type.getPresentableText());
            return errorProblem(literalValue, property, message);
        }

        ProblemDescriptor methodNotFoundProblem(PsiLiteralValue literalValue,
                                                Method method,
                                                PsiType type) {
            String message = Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.method.not.exist",
                    method.name(), type.getPresentableText());
            return errorProblem(literalValue, method, message);
        }

        ProblemDescriptor methodAtWritePositionProblem(PsiLiteralValue literalValue,
                                                       Method method) {
            return errorProblem(literalValue, method,
                    Lens4jBundle.getMessage("inspection.gen.lenses.lens.path.method.at.write.position"));
        }

        ProblemDescriptor errorProblem(PsiLiteralValue literalValue,
                                       PathPart part,
                                       String message) {
            return manager.createProblemDescriptor(
                    literalValue,
                    part.textRange().shiftRight(1),
                    message,
                    ProblemHighlightType.GENERIC_ERROR,
                    isOnTheFly,
                    (LocalQuickFix) null
            );
        }
    }

}
