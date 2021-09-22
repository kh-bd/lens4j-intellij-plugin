package dev.khbd.lens4j.intellij.common.path;

/**
 * Path parser.
 *
 * @author Sergei_Khadanovich
 */
public class PathParser {

    /**
     * Parse string into {@link Path} instance.
     *
     * @param pathStr path as string
     * @return parsed path
     */
    public Path parse(String pathStr) {
        Path path = new Path();

        if (pathStr.isEmpty()) {
            return path;
        }

        int start = 0;
        StringBuilder buffer = new StringBuilder();

        char[] chars = pathStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            if (current == '.') {
                if (buffer.length() != 0) {
                    path.addPart(new Property(buffer.toString(), start));
                    buffer = new StringBuilder();
                }
                path.addPart(new Point(i));
            } else {
                if (buffer.length() == 0) { // start new property
                    start = i;
                }
                buffer.append(current);
            }
        }


        if (buffer.length() != 0) {
            path.addPart(new Property(buffer.toString(), start));
        }

        return path;
    }
}
