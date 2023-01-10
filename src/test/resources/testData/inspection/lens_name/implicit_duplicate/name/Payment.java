package inspection.lens_name.implicit_duplicate.name;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.core.annotations.LensType;

@GenLenses(lenses = {
        @Lens(path = "amount"), // AMOUNT_READ_LENS
        @Lens(path = "amount.asLong", lensName = <error descr="Lens name is not unique">"AMOUNT_READ_LENS"</error>),
})
class Payment {
    Amount amount;
}

class Amount {
    Double asDouble;
    Long asLong;
}
