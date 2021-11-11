package inspection.path.method_at_last_position;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.core.annotations.LensType;

@GenLenses(lenses = {
        @Lens(path = "getTo()"),
        @Lens(path = "<error descr="Methods can not be used at last position in read-write lens">getTo</error>()", type = LensType.READ_WRITE),
        @Lens(path = "getTo().getCurrency()"),
        @Lens(path = "getTo().<error descr="Methods can not be used at last position in read-write lens">getCurrency</error>()", type = LensType.READ_WRITE),
})
class Payment {
    private Account to;

    Account getTo() {
        return to;
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
