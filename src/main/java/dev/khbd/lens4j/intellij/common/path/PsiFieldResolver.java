package dev.khbd.lens4j.intellij.common.path;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathVisitor;
import dev.khbd.lens4j.common.Property;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.Predicates;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Field resolver.
 *
 * <p>This resolver should be used only with correct path prefix. See
 * {@link PathService#getCorrectPathPrefix(Path)}
 *
 * @author Sergei_Khadanovich
 */
@RequiredArgsConstructor
public class PsiFieldResolver implements PathVisitor {

    public PsiFieldResolver(PsiClass rootClass) {
        this.currentClass = rootClass;
    }

    private PsiClass currentClass;
    private PsiField field;
    private boolean fail;

    @Override
    public void visitProperty(Property property) {
        if (fail) {
            return;
        }

        if (Objects.nonNull(field)) {
            PsiClass resolvedPsiClass = LensPsiUtil.resolveFieldClass(field)
                    .orElse(null);

            if (Objects.isNull(resolvedPsiClass)) {
                fail = true;
                return;
            }

            currentClass = resolvedPsiClass;
        }

        field = LensPsiUtil.findField(currentClass, Predicates.isStatic(false),
                Predicates.nameEquals(property.getName())).orElse(null);

        if (Objects.isNull(field)) {
            fail = true;
        }

    }

    /**
     * Check was path resolved or not.
     *
     * @return {@literal true} if path was resolved and {@literal false} otherwise
     */
    public boolean isResolved() {
        return Objects.nonNull(field) && !fail;
    }

    public PsiField getResolvedField() {
        if (fail) {
            return null;
        }
        return field;
    }

    /**
     * Get last resolved class.
     *
     * <p> If property was resolved, return enclosing class for that field.
     * If property was not resolved, return last resolved field enclosing class or root class.
     *
     * @return last resolved class
     */
    public PsiClass getResolvedClass() {
        return currentClass;
    }
}