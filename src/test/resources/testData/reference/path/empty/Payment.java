package reference.path.empty;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "<caret>"))
public class Payment {
    private String name;
}
