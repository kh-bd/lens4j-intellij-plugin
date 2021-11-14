package dev.khbd.lens4j.intellij.common;

import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiType;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class Predicates {

    public static final Predicate<PsiMethod> APPLICABLE_METHOD = method -> !method.hasParameters()
            && !PsiType.VOID.equals(method.getReturnType())
            && !method.isConstructor()
            && !method.getModifierList().hasExplicitModifier(PsiModifier.PRIVATE);

    public static Predicate<PsiMember> isStatic(boolean isStatic) {
        return m -> isStatic == m.getModifierList().hasExplicitModifier(PsiModifier.STATIC);
    }

    public static Predicate<PsiNamedElement> nameStarts(String prefix) {
        return nameF(name -> name.startsWith(prefix));
    }

    public static Predicate<PsiNamedElement> nameEquals(String expectedName) {
        return nameF(name -> name.equals(expectedName));
    }

    public static Predicate<PsiNamedElement> nameF(Predicate<String> nameF) {
        return named -> nameF.test(named.getName());
    }

    @SafeVarargs
    public static <T> Predicate<? super T> and(Predicate<? super T>... predicates) {
        return Arrays.stream(predicates)
                .reduce((p1, p2) -> f -> p1.test(f) && p2.test(f))
                .orElse(f -> true);
    }
}
