package completion.single.exist_properties_and_methods;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "amou<caret>"))
public class Payment extends BasePayment {
    private Double amount;

    public Double amount() {
        return amount;
    }
}
