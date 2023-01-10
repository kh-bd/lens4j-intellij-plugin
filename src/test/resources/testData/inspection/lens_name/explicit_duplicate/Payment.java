package inspection.lens_name.explicit_duplicate;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.core.annotations.LensType;

@GenLenses(lenses = {
        @Lens(path = "amount.asDouble", lensName = "AMOUNT"),
        @Lens(path = "amount.asLong", lensName = <error descr="Lens name is not unique">"AMOUNT"</error>),
        @Lens(path = "amount", lensName = <error descr="Lens name is not unique">"AMOUNT"</error>)
})
class Payment {
    Amount amount;
}

class Amount {
    Double asDouble;
    Long asLong;
}
