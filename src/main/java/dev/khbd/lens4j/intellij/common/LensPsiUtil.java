package dev.khbd.lens4j.intellij.common;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.MethodSignatureUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.containers.JBIterable;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * Resolve class by type.
     *
     * @param type type
     * @return resolved class or empty
     */
    public static Optional<PsiClass> resolvePsiClassByType(PsiType type) {
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            return Optional.ofNullable(classType.resolve());
        }
        return Optional.empty();
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
        Predicate<? super PsiField> predicate = Predicates.and(predicates);
        return Arrays.stream(psiClass.getAllFields())
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
        Predicate<? super PsiMethod> predicate = Predicates.and(predicates);

        PsiClass currentClass = psiClass;
        List<PsiMethod> result = new ArrayList<>();
        do {
            List<PsiMethod> methods = findApplicableMethodsInClass(currentClass, predicate);
            for (PsiMethod method : methods) {
                if (!existWithTheSameSignature(result, method)) {
                    result.add(method);
                }
            }
            currentClass = currentClass.getSuperClass();
        } while (Objects.nonNull(currentClass));

        return result;
    }

    private static boolean existWithTheSameSignature(List<PsiMethod> methods, PsiMethod method) {
        return methods.stream()
                .anyMatch(m -> MethodSignatureUtil.areSignaturesEqual(m, method));
    }

    private static List<PsiMethod> findApplicableMethodsInClass(PsiClass psiClass,
                                                                Predicate<? super PsiMethod> predicate) {
        return Stream.of(psiClass.getMethods())
                .filter(predicate)
                .collect(Collectors.toList());
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

    /**
     * Get {@link GenLenses} annotation from class.
     *
     * @param psiClass class
     */
    public static Optional<PsiAnnotation> genLenses(@NonNull PsiClass psiClass) {
        if (psiClass.isInterface()) {
            return Optional.empty();
        }
        return Optional.ofNullable(psiClass.getAnnotation(GenLenses.class.getName()));
    }

    /**
     * Get all declared lenses annotations from {@link GenLenses} annotation instance.
     *
     * @param genLenses gen lenses
     * @return all lenses
     */
    public static List<PsiAnnotation> lenses(@NonNull PsiAnnotation genLenses) {
        PsiAnnotationMemberValue lenses = genLenses.findAttributeValue("lenses");

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

    /**
     * Get all lenses annotation from class.
     *
     * @param psiClass class
     * @return list of lenses annotations
     */
    public static List<PsiAnnotation> lenses(@NonNull PsiClass psiClass) {
        return genLenses(psiClass)
                .map(LensPsiUtil::lenses)
                .orElseGet(Collections::emptyList);
    }

    /**
     * Check if lens is `write`.
     *
     * @param lens lens annotation
     * @return {@literal true} if lens is `write` and {@literal false} otherwise
     */
    public static boolean isWrite(PsiAnnotation lens) {
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

    /**
     * Check if lens is `read`.
     *
     * @param lens lens annotation
     * @return {@literal true} if lens is read and {@literal false} otherwise
     */
    public static boolean isRead(PsiAnnotation lens) {
        return !isWrite(lens);
    }
}
