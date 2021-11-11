package completion.ends_with_property.path_is_resolved.property_is_method;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "from.getBic<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
