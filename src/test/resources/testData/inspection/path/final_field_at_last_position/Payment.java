package inspection.path.final_field_at_last_position;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.core.annotations.LensType;

@GenLenses(lenses = {
        @Lens(path = "to"),
        @Lens(path = "<error descr="Final properties can not be used at last position in read-write lens">to</error>", type = LensType.READ_WRITE),
        @Lens(path = "to.number"),
        @Lens(path = "to.<error descr="Final properties can not be used at last position in read-write lens">number</error>", type = LensType.READ_WRITE)
})
class Payment {
    private final Account to;

    Payment(Account account) {
        this.to = account;
    }

    public Account getTo() {
        return to;
    }
}

class Account {
    private final String number;

    Account(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
