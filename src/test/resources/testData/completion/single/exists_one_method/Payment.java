package completion.single.exists_one_method;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "getA<caret>"))
public class Payment extends BasePayment {
    private Double amount;

    public Double getAmount() {
        return amount;
    }
}
