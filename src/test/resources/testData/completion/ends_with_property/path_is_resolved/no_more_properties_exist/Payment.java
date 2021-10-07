package completion.ends_with_property.path_is_resolved.no_more_properties_exist;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "from.bic<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
