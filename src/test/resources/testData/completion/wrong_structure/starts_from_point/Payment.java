package completion.wrong_structure.starts_from_point;

import complation.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = ".<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
