package reference.factory_field.factory_name_empty;

import dev.khbd.lens4j.core.Lenses;
import dev.khbd.lens4j.core.ReadLens;

public class PaymentLenses {

    public static final ReadLens<Payment, String> NAME_READ_LENS = Lenses.readLens(Payment::getName);

    private PaymentLenses() {
    }
}
