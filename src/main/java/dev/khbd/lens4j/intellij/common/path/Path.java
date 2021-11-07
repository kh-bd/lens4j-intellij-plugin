package dev.khbd.lens4j.intellij.common.path;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Sergei_Khadanovich
 */
@ToString
@EqualsAndHashCode
public class Path {

    private final List<PathPart> parts;

    public Path(List<PathPart> parts) {
        this.parts = new ArrayList<>(parts);
    }

    public Path() {
        this.parts = new ArrayList<>();
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    /**
     * Path length.
     *
     * @return path elements count
     */
    public int length() {
        return parts.size();
    }

    /**
     * Add part to path.
     *
     * <p>Be careful, this method mutates current instance.
     *
     * @param part part
     * @return self for chaining
     */
    public Path addPart(PathPart part) {
        parts.add(part);
        return this;
    }

    /**
     * Add parts to path.
     *
     * <p>Be careful, this method mutates current instance.
     *
     * @param parts parts
     * @return self for chaining
     */
    public Path addParts(PathPart... parts) {
        this.parts.addAll(List.of(parts));
        return this;
    }

    /**
     * Visit path with specified visitor.
     *
     * @param visitor visitor.
     */
    public void visit(PathVisitor visitor) {
        visitor.start();
        for (PathPart part : parts) {
            part.visit(visitor);
        }
        visitor.finish();
    }

    public PathPart getLastPart() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return parts.get(parts.size() - 1);
    }

    public PathPart getFirstPart() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return parts.get(0);
    }

    /**
     * Create new path without last part.
     *
     * @return new path without last part
     */
    public Path removeLastPart() {
        if (isEmpty()) {
            throw new IllegalStateException("Path is empty. Cannot remove last part");
        }
        List<PathPart> newParts = new ArrayList<>(parts);
        newParts.remove(newParts.size() - 1);
        return new Path(newParts);
    }

    /**
     * Make path copy.
     *
     * @return copied path instance
     */
    public Path copy() {
        return new Path(new ArrayList<>(parts));
    }
}


