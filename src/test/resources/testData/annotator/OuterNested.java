import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

class OuterNested {
    @GenLenses(factoryName = "<info>OuterNestedLenses</info>",
            lenses = @Lens(lensName = "<info>NAME_LENS</info>", path = "<info>name</info><info>.</info><info>length</info>"))
    class Nested {
        String name;
    }
}
