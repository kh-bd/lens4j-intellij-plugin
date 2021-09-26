package dev.khbd.lens4j.intellij.common;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.containers.JBIterable;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Sergei_Khadanovich
 */
public final class LensPsiUtil {

    public static final ElementPattern<PsiLiteralValue> LENS_PATH_PATTERN =
            literalInsideAnnotationParam(Lens.class, "path");

    public static final ElementPattern<PsiLiteralValue> LENS_NAME_PATTERN =
            literalInsideAnnotationParam(Lens.class, "lensName");

    public static final ElementPattern<PsiLiteralValue> LENS_FACTORY_NAME_PATTERN =
            literalInsideAnnotationParam(GenLenses.class, "factoryName");

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
     * @param isStatic  static or non-static field should be found
     * @return found field or null
     */
    public static Optional<PsiField> findField(PsiClass psiClass, String fieldName, boolean isStatic) {
        Predicate<PsiField> staticPredicate =
                field -> field.getModifierList().hasExplicitModifier(PsiModifier.STATIC);

        PsiField[] fields = psiClass.getAllFields();
        return Arrays.stream(fields)
                .filter(field -> isStatic == staticPredicate.test(field))
                .filter(field -> field.getName().equals(fieldName))
                .findFirst();
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

    /**
     * Find first enclosing class for specified element.
     *
     * @param element element
     * @return first enclosing class or empty
     */
    public static Optional<PsiClass> findFirstEnclosingClass(PsiElement element) {
        PsiClass firstClass =
                JBIterable.generate(element, PsiElement::getParent)
                        .filter(PsiClass.class)
                        .first();
        return Optional.ofNullable(firstClass);
    }

    /**
     * Find class with specified simple name in the same package as {@code psiClass}.
     *
     * @param psiClass   class
     * @param simpleName class simple name
     * @return found class or empty
     */
    public static Optional<PsiClass> findTheSamePackageClass(PsiClass psiClass, String simpleName) {
        return Optional.ofNullable(PsiUtil.getPackageName(psiClass))
                .flatMap(packageName -> {
                    Project project = psiClass.getProject();
                    JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                    String fqn = packageName + "." + simpleName;
                    PsiClass resolvedClass = facade.findClass(fqn, GlobalSearchScope.allScope(project));
                    return Optional.ofNullable(resolvedClass);
                });
    }

    private static ElementPattern<PsiLiteralValue> literalInsideAnnotationParam(
            Class<? extends Annotation> annotationClass,
            String parameterName
    ) {
        return PsiJavaPatterns.psiElement(PsiLiteralValue.class)
                .insideAnnotationParam(
                        StandardPatterns.string().equalTo(annotationClass.getCanonicalName()),
                        parameterName
                );
    }
}
