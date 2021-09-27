package dev.khbd.lens4j.intellij.common.path;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Field resolver.
 *
 * <p>This resolver should be used only with correct path prefix. See
 * {@link Path#getCorrectPathPrefix()}
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

        field = LensPsiUtil.findField(currentClass, property.getProperty(), false)
                .orElse(null);
        if (Objects.isNull(field)) {
            fail = true;
            return;
        }

        PsiClass resolvedPsiClass = LensPsiUtil.resolveFieldClass(field)
                .orElse(null);

        if (Objects.isNull(resolvedPsiClass)) {
            fail = true;
            return;
        }

        currentClass = resolvedPsiClass;
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
     * <p> If property was resolved, return field class.
     * If property was not resolved, return last resolved field class or root class.
     *
     * @return last resolved class
     */
    public PsiClass getResolvedClass() {
        return currentClass;
    }

    /**
     * Resolve field from root class.
     *
     * @param path      path to field
     * @param rootClass root class
     * @return resolved field or {@literal null} if any path part was not resolved
     */
    public static PsiField resolveField(Path path, PsiClass rootClass) {
        PsiFieldResolver resolver = new PsiFieldResolver(rootClass);
        path.visit(resolver);
        return resolver.getResolvedField();
    }
}