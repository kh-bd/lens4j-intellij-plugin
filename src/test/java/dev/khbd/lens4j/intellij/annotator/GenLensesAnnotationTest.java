package dev.khbd.lens4j.intellij.annotator;

import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class GenLensesAnnotationTest extends BaseIntellijTest {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/annotator";
    }

    @Test
    public void checkHighlighting_factoryAndLensNameAreFilled_annotateThem() {
        fixture.configureByFile("Entity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_factoryAndLensNameAreFilledAndClassIsPackagePrivate_annotateThem() {
        fixture.configureByFile("PackageEntity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_innerClass_annotateThem() {
        fixture.configureByFile("OuterInner.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_nestedClass_annotateThem() {
        fixture.configureByFile("OuterNested.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_emptyFactory_annotateThem() {
        fixture.configureByFile("EmptyFactoryEntity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_emptyLensName_annotateThem() {
        fixture.configureByFile("EmptyLensNameEntity.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void checkHighlighting_pathIsEmpty_notAnnotateIt() {
        fixture.configureByFile("EntityWithEmptyPath.java");

        fixture.checkHighlighting(true, true, true);
    }
}