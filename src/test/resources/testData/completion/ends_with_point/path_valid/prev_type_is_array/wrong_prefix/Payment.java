package completion.ends_with_point.path_valid.prev_type_is_array.wrong_prefix;

import completion.Account;
import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "accounts.siz<caret>"))
public class Payment {
    Account[] accounts;
}
