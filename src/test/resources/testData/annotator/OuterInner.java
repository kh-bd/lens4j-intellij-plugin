import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

class OuterInner {
    @GenLenses(factoryName = "<info>OuterInnerLenses</info>",
            lenses = @Lens(lensName = "<info>NAME_LENS</info>", path = "<info>name</info><info>.</info><info>length</info>"))
    class Inner {
        String name;
    }
}
