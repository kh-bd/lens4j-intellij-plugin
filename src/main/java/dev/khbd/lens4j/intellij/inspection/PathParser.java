package dev.khbd.lens4j.intellij.inspection;

/**
 * @author Sergei_Khadanovich
 */
class PathParser {

    /**
     * Parse string literal into chunks.
     *
     * @param path path
     * @return parsed path
     */
    Path parse(String path) {
        return parse(0, path, new Path());
    }

    private Path parse(int startFrom, String pathStr, Path path) {
        int index = pathStr.indexOf('.', startFrom);
        if (index == -1) {
            // last part
            PathPart part = new PathPart(pathStr.substring(startFrom), startFrom, pathStr.length() - 1);
            return path.addPart(part);
        }

        PathPart part = new PathPart(pathStr.substring(startFrom, index), startFrom, index - 1);
        return parse(index + 1, pathStr, path.addPart(part));
    }

}
