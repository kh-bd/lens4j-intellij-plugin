package dev.khbd.lens4j.intellij.common;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public final class PsiUtil {

    private PsiUtil() {
    }

    /**
     * Check is class nested or not.
     *
     * @param psiClass class to check
     * @return {@literal true} if class is nested and {@literal false} otherwise
     */
    public static boolean isNested(PsiClass psiClass) {
        PsiClass containingClass = psiClass.getContainingClass();
        return Objects.nonNull(containingClass);
    }

    /**
     * Find non-static field with specified name.
     *
     * @param psiClass  class
     * @param fieldName field name
     * @return found field or null
     */
    public static PsiField findNonStaticField(PsiClass psiClass, String fieldName) {
        PsiField[] fields = psiClass.getAllFields();
        return Arrays.stream(fields)
                .filter(field -> !field.getModifierList().hasExplicitModifier(PsiModifier.STATIC))
                .filter(field -> field.getName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Extract all {@link Lens} annotations from specified class.
     *
     * @param psiClass class
     * @return extracted annotations
     */
    public static List<PsiAnnotation> findLensAnnotations(PsiClass psiClass) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getName());
        if (Objects.isNull(genLens)) {
            return List.of();
        }

        return findLensAnnotations(genLens);
    }

    private static List<PsiAnnotation> findLensAnnotations(PsiAnnotation genLens) {
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
}
