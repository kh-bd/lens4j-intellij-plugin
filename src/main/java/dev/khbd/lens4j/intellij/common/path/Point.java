package dev.khbd.lens4j.intellij.common.path;

import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class Point implements PathPart {

    int position;

    @Override
    public void visit(PathVisitor visitor) {
        visitor.visitPoint(this);
    }

    @Override
    public PathPartKind getKind() {
        return PathPartKind.POINT;
    }
}
