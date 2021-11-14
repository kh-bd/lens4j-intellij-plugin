package completion.duplicate_methods;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = @Lens(path = "<caret>"))
public class Payment extends AbstractPayment {
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    @Override
    public double sum() {
        return super.sum();
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 1;
    }
}

class AbstractPayment {

    int sum() {
        return 1.0;
    }
}
