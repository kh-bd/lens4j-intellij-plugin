package reference.factory;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(factoryName = "PaymentLenses<caret>", lenses = @Lens(path = "name"))
public class Payment {

    private String name;

    public String getName() {
        return name;
    }
}
