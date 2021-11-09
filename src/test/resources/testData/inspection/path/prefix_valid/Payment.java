package inspection.path.prefix_valid;

import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(lenses = {
        @Lens(path = "to"),
        @Lens(path = "to.accountNumber"),
        @Lens(path = "to.<error descr="Property bic does not exist in type Account">bic</error>")
})
class WrongProperty {
    private Account to;
}

@GenLenses(lenses = @Lens(path = "<error descr="Method to does not exist in type NoMethod">to</error>().accountNumber"))
class NoMethod {
    private Account to;
}

@GenLenses(lenses = @Lens(path = "to.<error descr="Method accountNumber does not exist in type Account">accountNumber</error>()"))
class MethodIsPrivate {
    private Account to;
}

@GenLenses(lenses = @Lens(path = "<error descr="Method getTo does not exist in type MethodIsStatic">getTo</error>().accountNumber"))
class MethodIsStatic {

    public static Account getTo() {
        return null;
    }

    private Account to;
}

@GenLenses(lenses = @Lens(path = "<error descr="Method getTo does not exist in type MethodReturnTypeIsVoid">getTo</error>().accountNumber"))
class MethodReturnTypeIsVoid {

    public static void getTo() {
        System.out.println("get to");
    }

    private Account to;
}

@GenLenses(lenses = @Lens(path = "<error descr="Method getTo does not exist in type MethodHasArguments">getTo</error>().accountNumber"))
class MethodHasArguments {

    public Account getTo(boolean main) {
        return null;
    }

    private Account to;
}

class Account {
    private String accountNumber;

    private String accountNumber() {
        return accountNumber;
    }
}


