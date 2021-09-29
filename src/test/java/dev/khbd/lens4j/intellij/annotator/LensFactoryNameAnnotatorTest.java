package dev.khbd.lens4j.intellij.annotator;

import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryNameAnnotatorTest extends BaseIntellijTest {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/annotator/lens_factory";
    }

    @Test
    public void factoryNameIsNotEmpty_checkHighlighting_factoryNameHighlightedAsInfo() {
        fixture.configureByFile("EntityWithNotEmptyFactoryName.java");

        fixture.checkHighlighting(true, true, true);
    }

    @Test
    public void factoryNameIsEmpty_checkHighlighting_nothingToHighlight() {
        fixture.configureByFile("EntityWithEmptyFactoryName.java");

        fixture.checkHighlighting(true, true, true);
    }
}