package reference.path.correct.array_length;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import reference.path.correct.Account;

@GenLenses(lenses = @Lens(path = "accounts.length<caret>"))
class Payment {
    private Account[] accounts;

    Account[] getAccounts() {
        return accounts;
    }
}
