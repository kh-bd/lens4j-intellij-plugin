package dev.khbd.lens4j.intellij.common.path;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.light.LightFieldBuilder;
import com.intellij.psi.impl.light.LightPsiClassBuilder;
import com.intellij.psi.util.PsiTypesUtil;
import dev.khbd.lens4j.common.Method;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathPart;
import dev.khbd.lens4j.common.PathVisitor;
import dev.khbd.lens4j.common.Property;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.Predicates;
import lombok.Getter;
import lombok.Value;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class PsiMemberResolver implements PathVisitor {

    public PsiMemberResolver(PsiClass rootClass) {
        this.rootClassType = PsiTypesUtil.getClassType(rootClass);
    }

    private final PsiClassType rootClassType;
    private final LinkedList<ResolvedPart> resolvedHistory = new LinkedList<>();

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

        PsiType lastResolvedType = getLastResolvedType();
        if (lastResolvedType instanceof PsiArrayType) {
            visitArrayProperty(property);
        } else {
            visitClassProperty(property);
        }
    }

    private void visitArrayProperty(Property property) {
        if (!property.getName().equals("length")) {
            fail(property);
            return;
        }

        PsiManager manager = getResolvedMember().getManager();
        LightFieldBuilder lengthField = new LightFieldBuilder(manager, "length", PsiType.INT);
        lengthField.setModifiers(PsiModifier.PUBLIC, PsiModifier.FINAL);
        lengthField.setContainingClass(new LightPsiClassBuilder(getResolvedMember(), "__Array__"));
        resolvedHistory.addLast(new ResolvedPart(property, lengthField, PsiType.INT));
    }

    private void visitClassProperty(Property property) {
        PsiClass psiClass = getLastResolvedClass();
        if (Objects.isNull(psiClass)) {
            fail(property);
            return;
        }

        PsiField field = LensPsiUtil.findField(psiClass, Predicates.isStatic(false),
                Predicates.nameEquals(property.getName())).orElse(null);

        if (Objects.isNull(field)) {
            fail(property);
            return;
        }

        resolvedHistory.addLast(new ResolvedPart(property, field, field.getType()));
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

        resolvedHistory.addLast(new ResolvedPart(method, psiMethod, psiMethod.getReturnType()));
    }

    private PsiClass getLastResolvedClass() {
        return LensPsiUtil.resolvePsiClassByType(getLastResolvedType()).orElse(null);
    }

    private PsiMethod findMethod(PsiClass psiClass, Method method) {
        return LensPsiUtil.findMethod(psiClass, Predicates.isStatic(false),
                        Predicates.APPLICABLE_METHOD,
                        Predicates.nameEquals(method.getName()))
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
        return !(fail || resolvedHistory.isEmpty());
    }

    /**
     * If path was fully resolved, returns last resolved psi member.
     * If resolving process was failed, returns {@literal null}.
     */
    public PsiMember getResolvedMember() {
        if (fail) {
            return null;
        }
        return resolvedHistory.getLast().getMember();
    }

    /**
     * Return last resolved type or root class type if nothing was resolved.
     */
    public PsiType getLastResolvedType() {
        if (resolvedHistory.isEmpty()) {
            return rootClassType;
        }
        return resolvedHistory.getLast().getType();
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

    @Value
    private static class ResolvedPart {
        PathPart part;
        PsiMember member;
        PsiType type;
    }
}
