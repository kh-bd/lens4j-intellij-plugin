package annotator;

import java.util.List;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(factoryName = "<info>EntityLenses</info>",
        lenses = @Lens(lensName = "<info>NAME_LENS</info>", path = "<info>names</info><info>()</info><info>.</info><info>size</info><info>()</info>"))
public class PathWithMethods {
    List<String> names() {
        return List.of("hello");
    }
}
