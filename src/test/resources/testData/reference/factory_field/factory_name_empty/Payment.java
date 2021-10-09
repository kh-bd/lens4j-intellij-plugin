package reference.factory_field.factory_name_empty;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(lensName = "NAME_READ_LENS<caret>", path = "name"))
public class Payment {

    private String name;

    public String getName() {
        return name;
    }
}
