package reference.factory_field.field_found;

import dev.khbd.lens4j.core.Lenses;
import dev.khbd.lens4j.core.ReadLens;

public class PaymentLenses {

    public static final ReadLens<Payment, String> NAME_READ_LENS = Lenses.readLens(Payment::getName);

    private PaymentLenses() {
    }
}
