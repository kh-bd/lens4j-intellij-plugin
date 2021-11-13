package inspection.path.array_length;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;
import dev.khbd.lens4j.core.annotations.LensType;

@GenLenses(lenses = {
        @Lens(path = "accounts.length"),
        @Lens(path = "accounts.<error descr="Final properties can not be used at last position in read-write lens">length</error>", type = LensType.READ_WRITE),
        @Lens(path = "accounts.<error descr="Property size does not exist in type Account[]">size</error>"),
        @Lens(path = "accounts.length.<error descr="Property toByte does not exist in type int">toByte</error>"),
})
class Payment {
    private Account[] accounts;

    public Account[] getAccounts() {
        return accounts;
    }
}

class Account {
    private String accountNumber;

    public String getAccountNumber() {
        return accountNumber;
    }
}
