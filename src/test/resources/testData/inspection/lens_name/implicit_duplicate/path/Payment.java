package inspection.lens_name.implicit_duplicate.path;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.core.annotations.LensType;

@GenLenses(lenses = {
        @Lens(path = "amount.asLong", lensName = "AMOUNT_READ_LENS"),
        @Lens(path = <error descr="Auto-derived lens name is AMOUNT_READ_LENS. It is not unique">"amount"</error>)
})
class Payment {
    Amount amount;
}

class Amount {
    Double asDouble;
    Long asLong;
}
