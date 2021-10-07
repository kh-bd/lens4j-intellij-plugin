package completion.ends_with_property.path_is_not_resolved.remain_path_is_not_resolved;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "child.from<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
