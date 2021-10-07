package completion.emtpy_path;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
