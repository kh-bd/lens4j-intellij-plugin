package dev.khbd.lens4j.intellij.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import lombok.Value;
import lombok.experimental.Delegate;

import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class TypedPathPart {
    @Delegate
    RawPathPart base;
    PsiClass psiClass;

    /**
     * Can be {@literal null}, if property was not found in {@code psiClass}.
     */
    PsiField field;

    public boolean isFieldResolved() {
        return Objects.nonNull(field);
    }

    public boolean isFieldUnResolved() {
        return !isFieldResolved();
    }
}
