package inspection.path.prefix_valid;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = {
        @Lens(path = "to"),
        @Lens(path = "to.accountNumber"),
        @Lens(path = "to.<error descr="Property bic does not exist in class Account">bic</error>")
})
class Payment {
    private Account to;
}

class Account {
    private String accountNumber;
}


