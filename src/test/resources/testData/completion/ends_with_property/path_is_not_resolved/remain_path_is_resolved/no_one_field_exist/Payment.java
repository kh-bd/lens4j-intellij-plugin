package completion.ends_with_property.path_is_not_resolved.remain_path_is_resolved.exists_one_field_with_prefix;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "from.prefix<caret>"))
public class Payment extends BasePayment {
    private Double amount;
}
