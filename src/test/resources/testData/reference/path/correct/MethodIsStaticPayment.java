package reference.path.correct;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "account<caret>().getCurrency()"))
class MethodIsStaticPayment {
    private Account account;

    static Account getAccount() {
        return null;
    }
}