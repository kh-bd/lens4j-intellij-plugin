package completion.single.exist_more_than_one_methods;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "get<caret>"))
public class Payment extends BasePayment {
    private Double amount;

    public Double getAmount() {
        return amount;
    }
}
