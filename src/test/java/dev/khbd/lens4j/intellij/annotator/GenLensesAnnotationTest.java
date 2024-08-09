package dev.khbd.lens4j.intellij.annotator;

import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.junit.jupiter.api.Test;

/**
 * @author Sergei_Khadanovich
 */
public class GenLensesAnnotationTest extends BaseIntellijTest {

    @Test
    public void checkHighlighting_factoryAndLensNameAreFilled_annotateThem() {
        fixture.configureByFile("annotator/Entity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_pathContainsMethods_annotateThem() {
        fixture.configureByFile("annotator/PathWithMethods.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_factoryAndLensNameAreFilledAndClassIsPackagePrivate_annotateThem() {
        fixture.configureByFile("annotator/PackageEntity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_innerClass_annotateThem() {
        fixture.configureByFile("annotator/OuterInner.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_nestedClass_annotateThem() {
        fixture.configureByFile("annotator/OuterNested.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_emptyFactory_annotateThem() {
        fixture.configureByFile("annotator/EmptyFactoryEntity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_emptyLensName_annotateThem() {
        fixture.configureByFile("annotator/EmptyLensNameEntity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_pathIsEmpty_notAnnotateIt() {
        fixture.configureByFile("annotator/EntityWithEmptyPath.java");

        fixture.checkHighlighting(true, true, true);
    }
}