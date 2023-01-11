package dev.khbd.lens4j.intellij.common.path.grammar;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class PathBuilder {

    private final List<PathPart> parts = new ArrayList<>();

    PathBuilder() {
    }

    /**
     * Add part to path.
     *
     * @param part path part
     * @return self
     */
    public PathBuilder withPart(@NonNull PathPart part) {
        parts.add(part);
        return this;
    }

    /**
     * Add parts to path.
     *
     * @param parts path parts
     * @return self
     */
    public PathBuilder withParts(PathPart... parts) {
        for (PathPart part : parts) {
            withPart(part);
        }
        return this;
    }

    /**
     * Check if builder is empty or not.
     */
    public boolean isEmpty() {
        return parts.isEmpty();
    }

    /**
     * Get last accumulated part.
     *
     * @return last path part
     * @throws RuntimeException if builder is empty
     */
    public PathPart getLast() {
        if (isEmpty()) {
            throw new RuntimeException("Builder is empty");
        }
        return parts.get(parts.size() - 1);
    }

    /**
     * Build resulted path.
     *
     * @return path
     */
    public Path build() {
        return new Path(parts);
    }
}
