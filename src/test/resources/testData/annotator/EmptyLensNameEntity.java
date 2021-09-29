import dev.khbd.lens4j.core.annotations.GenLenses;
import dev.khbd.lens4j.core.annotations.Lens;

@GenLenses(factoryName = "<info>Factory</info>", lenses = @Lens(lensName = "", path = "<info>name</info><info>.</info><info>length</info>"))
class EmptyLensNameEntity {
    String name;
}
