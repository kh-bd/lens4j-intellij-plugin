package completion.single.exist_more_than_one_properties;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "fro<caret>"))
public class Payment extends BasePayment {
    private Double amount;
    private String fromChild;
}
