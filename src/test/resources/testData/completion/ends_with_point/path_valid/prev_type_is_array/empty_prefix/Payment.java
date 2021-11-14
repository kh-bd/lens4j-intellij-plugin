package completion.ends_with_point.path_valid.prev_type_is_array.empty_prefix;

import completion.Account;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "accounts.<caret>"))
public class Payment {
    Account[] accounts;
}
