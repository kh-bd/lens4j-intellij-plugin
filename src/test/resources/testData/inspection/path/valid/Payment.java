package inspection.path.valid;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = {
        @Lens(path = "from"),
        @Lens(path = "from.currency"),
        @Lens(path = "from.currency.code"),
        @Lens(path = "to"),
        @Lens(path = "to.currency"),
        @Lens(path = "to.currency.code")
})
class Payment extends BasePayment {
    private Account to;
}

class BasePayment {
    private Account from;
}

class Account {
    private Currency currency;
}

class Currency {
    private String code;
}

