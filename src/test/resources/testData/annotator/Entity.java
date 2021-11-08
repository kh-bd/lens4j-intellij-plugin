package annotator;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(factoryName = "<info>EntityLenses</info>",
        lenses = @Lens(lensName = "<info>NAME_LENS</info>", path = "<info>name</info><info>.</info><info>length</info>"))
public class Entity {
    String name;
}
