package dev.khbd.lens4j.intellij.common.path;

import com.intellij.openapi.util.TextRange;
import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class Property implements PathPart {
    String property;
    int start;

    @Override
    public void visit(PathVisitor visitor) {
        visitor.visitProperty(this);
    }

    @Override
    public PathPartKind getKind() {
        return PathPartKind.PROPERTY;
    }

    @Override
    public TextRange getTextRange() {
        return new TextRange(start, start + property.length());
    }
}
