package reference.path.correct;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "getAccount().getCurrency<caret>()"))
public class ExactMethodPayment {
    private Account account;

    Account getAccount() {
        return account;
    }
}