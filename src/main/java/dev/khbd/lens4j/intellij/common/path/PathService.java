package dev.khbd.lens4j.intellij.common.path;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.util.TextRange;
import lombok.NonNull;

/**
 * @author Sergei_Khadanovich
 */
@Service
public final class PathService {

    public static PathService getInstance() {
        return ApplicationManager.getApplication().getService(PathService.class);
    }

    /**
     * Evaluate text range for specified path part.
     *
     * @param part path part
     * @return text range
     */
    public TextRange getTextRange(@NonNull PathPart part) {
        PathPartKind kind = part.getKind();
        if (kind == PathPartKind.POINT) {
            return getTextRange((Point) part);
        } else if (kind == PathPartKind.PROPERTY) {
            return getTextRange((Property) part);
        }
        throw new IllegalArgumentException("Unsupported path part");
    }

    /**
     * Evaluate text range for specified point.
     *
     * @param point path point
     * @return text range
     */
    public TextRange getTextRange(@NonNull Point point) {
        return new TextRange(point.getPosition(), point.getPosition() + 1);
    }

    /**
     * Evaluate text range for specified property.
     *
     * @param property path property
     * @return text range
     */
    public TextRange getTextRange(@NonNull Property property) {
        return new TextRange(property.getStart(), property.getStart() + property.getProperty().length());
    }
}
