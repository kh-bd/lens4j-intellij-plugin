package dev.khbd.lens4j.intellij.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLiteralExpression;
import dev.khbd.lens4j.core.annotations.GenLenses;
import lombok.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
@Service
public final class FactoryNameResolver {

    private static final String FACTORY_NAME_SUFFIX = "Lenses";

    public static FactoryNameResolver getInstance() {
        return ApplicationManager.getApplication().getService(FactoryNameResolver.class);
    }

    /**
     * Resolve factory name or infer default factory name.
     *
     * <p>This method return empty value if class is not annotated with
     * {@link GenLenses}. In other cases, it returns explicitly supplied factory name or
     * implicitly inferred default factory name.
     *
     * @param psiClass class
     * @return factory name or empty
     */
    public Optional<String> resolveFactoryName(@NonNull PsiClass psiClass) {
        PsiAnnotation genLens = psiClass.getAnnotation(GenLenses.class.getCanonicalName());
        if (Objects.isNull(genLens)) {
            return Optional.empty();
        }

        // don't have to check for nullability, there is a default value
        PsiAnnotationMemberValue factoryNameValue = genLens.findAttributeValue("factoryName");

        // compiler can guarantee that factoryName contains only string literals,
        // but users can write what they want, for example, @GenLenses(factoryName = 2 + 2).
        // In such cases, factoryNameValue is not a literal expression.
        if (!(factoryNameValue instanceof PsiLiteralExpression)) {
            return Optional.of(inferDefaultFactoryName(psiClass));
        }

        return LensPsiUtil.getStringValue((PsiLiteralExpression) factoryNameValue)
                .filter(factoryName -> !factoryName.isBlank())
                .or(() -> Optional.of(inferDefaultFactoryName(psiClass)));
    }

    private String inferDefaultFactoryName(PsiClass psiClass) {
        return psiClass.getName() + FACTORY_NAME_SUFFIX;
    }
}
