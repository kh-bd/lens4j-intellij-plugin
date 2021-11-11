package completion.ends_with_property.path_is_resolved.more_members_exist;

import completion.BasePayment;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "currency.code<caret>"))
public class Payment extends BasePayment {
    private Double amount;
    private Currency currency;
}

class Currency {
    private String code;
    private String code1;
    private String code2;

    public String codeM() {
        return code;
    }
}
