package inspection.wrong_place.on_gen_class;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

<error descr="@GenLenses is not allowed on generic classes">@GenLenses(lenses = @Lens(path = "name"))</error>
public class AbstractPayment<F> {
    private F from;
}
