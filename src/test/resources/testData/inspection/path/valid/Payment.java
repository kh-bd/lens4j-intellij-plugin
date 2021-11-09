package inspection.path.valid;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = {
        @Lens(path = "from"),
        @Lens(path = "getFrom()"),
        @Lens(path = "from.currency"),
        @Lens(path = "getFrom().getCurrency()"),
        @Lens(path = "from.currency.code"),
        @Lens(path = "from.getCurrency().code"),
        @Lens(path = "to"),
        @Lens(path = "to.currency"),
        @Lens(path = "to.currency.code")
})
class Payment extends BasePayment {
    private Account to;

    Account getTo() {
        return to;
    }
}

class BasePayment {
    private Account from;

    Account getFrom() {
        return from;
    }
}

class Account {
    private Currency currency;

    Currency getCurrency() {
        return currency;
    }
}

class Currency {
    private String code;

    String getCode() {
        return code;
    }
}

