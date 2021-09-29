package package1;

import dev.khbd.lens4j.core.annotations.GenLenses;

@GenLenses(factoryName = "<info>FactoryName</info>")
public class EntityWithNotEmptyFactoryName {

    String name;
}