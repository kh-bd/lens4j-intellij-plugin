package dev.khbd.lens4j.intellij.common;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.containers.JBIterable;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Resolved field class.
     *
     * @param field field
     * @return field class or empty
     */
    public static Optional<PsiClass> resolveFieldClass(PsiField field) {
        PsiType type = field.getType();
        if (!(type instanceof PsiClassType)) {
            return Optional.empty();
        }

        PsiClassType classType = (PsiClassType) field.getType();
        return Optional.ofNullable(classType.resolve());
    }

    /**
     * Find all fields by predicates.
     *
     * @param psiClass   class
     * @param predicates field predicates
     * @return fields
     */
    @SafeVarargs
    public static List<PsiField> findFields(PsiClass psiClass,
                                            Predicate<? super PsiField>... predicates) {
        return findElements(psiClass, PsiClass::getAllFields, predicates);
    }

    /**
     * Find all elements by predicates.
     *
     * @param psiClass   class
     * @param predicates element predicates
     * @return elements
     */
    @SafeVarargs
    private static <M extends PsiElement> List<M> findElements(PsiClass psiClass,
                                                               Function<? super PsiClass, M[]> extractor,
                                                               Predicate<? super M>... predicates) {
        Predicate<? super M> predicate =
                Arrays.stream(predicates)
                        .reduce((p1, p2) -> f -> p1.test(f) && p2.test(f))
                        .orElse(f -> true);
        M[] elements = extractor.apply(psiClass);
        return Arrays.stream(elements)
                .filter(predicate)
                .collect(Collectors.toList());
    }


    /**
     * Find field by predicates.
     *
     * @param psiClass   class
     * @param predicates field predicates
     * @return field
     */
    @SafeVarargs
    public static Optional<PsiField> findField(PsiClass psiClass,
                                               Predicate<? super PsiField>... predicates) {
        return findFields(psiClass, predicates).stream().findFirst();
    }

    /**
     * Find all methods by predicates.
     *
     * @param psiClass   class
     * @param predicates method predicates
     * @return methods
     */
    @SafeVarargs
    public static List<PsiMethod> findMethods(PsiClass psiClass,
                                              Predicate<? super PsiMethod>... predicates) {
        return findElements(psiClass, PsiClass::getAllMethods, predicates);
    }

    /**
     * Find method by predicates.
     *
     * @param psiClass   class
     * @param predicates method predicates
     * @return method
     */
    @SafeVarargs
    public static Optional<PsiMethod> findMethod(PsiClass psiClass,
                                                 Predicate<? super PsiMethod>... predicates) {
        return findMethods(psiClass, predicates).stream().findFirst();
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
