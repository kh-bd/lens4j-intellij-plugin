package dev.khbd.lens4j.intellij.common.path;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import dev.khbd.lens4j.common.Method;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathPart;
import dev.khbd.lens4j.common.PathVisitor;
import dev.khbd.lens4j.common.Property;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class PsiMemberResolver implements PathVisitor {

    public PsiMemberResolver(PsiClass rootClass) {
        this.lastResolvedType = PsiTypesUtil.getClassType(rootClass);
    }

    /**
     * Last resolved member can be null. It can be in such cases:
     * <ul>
     * <li>Path is correct, but no one part has been resolved yet.
     * <li>Path is fully incorrect, first part was not resolved.
     */
    @Getter
    private PsiMember lastResolvedMember;

    /**
     * Last resolved type can not be null. It contains last resolved member type or, if
     * no one part has been resolved yet, contains root class type.
     */
    @Getter
    private PsiType lastResolvedType;

    private boolean fail;

    /**
     * This part is null, if whole path was resolved.
     */
    @Getter
    private PathPart nonResolvedPart;

    @Override
    public void visitProperty(Property property) {
        if (fail) {
            return;
        }

        PsiClass psiClass = getLastResolvedClass();
        if (Objects.isNull(psiClass)) {
            fail(property);
            return;
        }

        PsiField field = LensPsiUtil.findField(psiClass, property.getName(), false)
                .orElse(null);

        if (Objects.isNull(field)) {
            fail(property);
            return;
        }

        lastResolvedType = field.getType();
        lastResolvedMember = field;
    }

    @Override
    public void visitMethod(Method method) {
        if (fail) {
            return;
        }

        PsiClass psiClass = getLastResolvedClass();
        if (Objects.isNull(psiClass)) {
            fail(method);
            return;
        }

        PsiMethod psiMethod = findMethod(psiClass, method);

        if (Objects.isNull(psiMethod)) {
            fail(method);
            return;
        }

        lastResolvedType = psiMethod.getReturnType();
        lastResolvedMember = psiMethod;
    }

    private PsiClass getLastResolvedClass() {
        if (!(lastResolvedType instanceof PsiClassType)) {
            return null;
        }
        PsiClassType classType = (PsiClassType) lastResolvedType;
        return classType.resolve();
    }

    private PsiMethod findMethod(PsiClass psiClass, Method method) {
        List<PsiMethod> methods = LensPsiUtil.findAllMethodsWithName(psiClass, method.getName(), false);

        return methods.stream()
                .filter(m -> !m.hasParameters())
                .filter(m -> !PsiType.VOID.equals(m.getReturnType()))
                .filter(m -> !m.getModifierList().hasExplicitModifier(PsiModifier.PRIVATE))
                .findFirst()
                .orElse(null);
    }

    private void fail(PathPart part) {
        fail = true;
        nonResolvedPart = part;
    }

    /**
     * Check was path resolved or not.
     *
     * @return {@literal true} if path was resolved and {@literal false} otherwise
     */
    public boolean isResolved() {
        return Objects.nonNull(lastResolvedMember) && !fail;
    }

    /**
     * If path was fully resolved, returns last resolved psi member.
     * If resolving process was failed, returns {@literal null}.
     */
    public PsiMember getResolvedMember() {
        if (fail) {
            return null;
        }
        return lastResolvedMember;
    }

    /**
     * Resolve member from root class.
     *
     * @param path      path to member
     * @param rootClass root class
     * @return resolved member or {@literal null} if any path part was not resolved
     */
    public static PsiMember resolveMember(Path path, PsiClass rootClass) {
        PsiMemberResolver resolver = new PsiMemberResolver(rootClass);
        path.visit(resolver);
        return resolver.getResolvedMember();
    }
}
