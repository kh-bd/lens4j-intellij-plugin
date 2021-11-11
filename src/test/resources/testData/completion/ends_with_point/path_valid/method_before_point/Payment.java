package completion.ends_with_point.path_valid.method_before_point;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "getFrom().<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
