package reference.path.wrong_structure;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = ".name<caret>"))
public class Payment {
    private String name;
}
