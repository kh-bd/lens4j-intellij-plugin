package completion.single.not_exist;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "acc<caret>"))
public class Payment extends BasePayment {
    private Double amount;

    public Double getAmount() {
        return amount;
    }
}
