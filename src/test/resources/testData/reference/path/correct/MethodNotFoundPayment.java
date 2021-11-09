package reference.path.correct;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "account<caret>().getCurrency()"))
public class MethodNotFoundPayment {
    private Account account;

    Account getAccount() {
        return account;
    }
}