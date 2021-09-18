package dev.khbd.lens4j.intellij.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiModifier;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public final class LensPsiUtil {

    private LensPsiUtil() {
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
     * Get string value from psi literal.
     *
     * @param literalValue psi literal
     * @return string value of empty
     */
    public static Optional<String> getStringValue(PsiLiteralValue literalValue) {
        return Optional.ofNullable(literalValue.getValue())
                .flatMap(value -> {
                    if (!(value instanceof String)) {
                        return Optional.empty();
                    }
                    return Optional.of((String) value);
                });
    }
}
