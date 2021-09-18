package dev.khbd.lens4j.intellij.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;

import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class PathParser {

    /**
     * Parse string literal to raw path.
     *
     * @param path path
     * @return parsed path
     */
    public RawPath rawParse(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new IllegalArgumentException("path should be not blank");
        }
        return rawParse(0, path, new RawPath());
    }

    private RawPath rawParse(int startFrom, String pathStr, RawPath path) {
        int index = pathStr.indexOf('.', startFrom);
        if (index == -1) {
            // last part
            RawPathElement element = new RawPathElement(pathStr.substring(startFrom), startFrom, pathStr.length() - 1);
            return path.addElement(element);
        }

        RawPathElement element = new RawPathElement(pathStr.substring(startFrom, index), startFrom, index - 1);
        return rawParse(index + 1, pathStr, path.addElement(element));
    }

    /**
     * Parse string literal to psi path.
     *
     * @param path      string path
     * @param rootClass root class
     * @return psi path
     */
    public PsiPath psiParse(String path, PsiClass rootClass) {
        RawPath rawPath = rawParse(path);

        PsiClass currentPsiClass = rootClass;

        PsiPath result = new PsiPath();

        for (RawPathElement rawElement : rawPath) {
            PsiField field = LensPsiUtil.findNonStaticField(currentPsiClass, rawElement.getProperty());

            if (Objects.nonNull(field)) {
                result.addElement(new PsiPathElement(rawElement, currentPsiClass, field));

                PsiClassType classType = (PsiClassType) field.getType();
                PsiClass resolvedPsiClass = classType.resolve();

                if (Objects.isNull(resolvedPsiClass)) {
                    // field class can not be resolved by some reason,
                    // nothing can be done more
                    break;
                }

                currentPsiClass = resolvedPsiClass;
            } else {
                result.addElement(new PsiPathElement(rawElement, currentPsiClass, null));
                // if current field was not found, nothing can be done more.
                break;
            }
        }

        return result;
    }
}
