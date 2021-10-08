package inspection.path.path_is_empty;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = <error descr="Blank path is not allowed">""</error>))
public class Payment {
    private String name;
}