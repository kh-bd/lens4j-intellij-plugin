package dev.khbd.lens4j.intellij.common.path;

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
}
