package reference.factory_field.field_found;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(factoryName = "PaymentLenses", lenses = @Lens(lensName = "NAME_READ_LENS<caret>", path = "name"))
public class Payment {

    private String name;

    public String getName() {
        return name;
    }
}
