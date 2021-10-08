package reference.path.correct;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "account.currency.isoCode<caret>"))
public class AccountLensPayment {
    private Account account;
}