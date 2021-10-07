package completion.ends_with_point.path_not_valid;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "child.<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
