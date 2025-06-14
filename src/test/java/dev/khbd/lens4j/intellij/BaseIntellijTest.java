package dev.khbd.lens4j.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergei_Khadanovich
 */
public abstract class BaseIntellijTest {

    protected CodeInsightTestFixture fixture;

    @BeforeEach
    public void beforeEach() throws Exception {
        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder =
                factory.createLightFixtureBuilder(getProjectDescriptor(), "lens4j");

        IdeaProjectTestFixture projectFixture = fixtureBuilder.getFixture();

        fixture = IdeaTestFixtureFactory.getFixtureFactory()
                .createCodeInsightFixture(projectFixture, getTempDirFixture());

        fixture.setTestDataPath(getTestDataPath());
        fixture.setUp();

        LanguageLevelProjectExtension.getInstance(getProject())
                .setLanguageLevel(getLanguageLevel());

        LensTestUtil.loadLens4jLib(fixture.getProjectDisposable(), fixture.getModule());
    }

    @AfterEach
    public void afterEach() throws Exception {
        try {
            fixture.tearDown();
        } finally {
            fixture = null;
        }
    }

    protected String getTestDataPath() {
        return "src/test/resources/testData";
    }

    protected LanguageLevel getLanguageLevel() {
        return LanguageLevel.JDK_11;
    }

    protected LightProjectDescriptor getProjectDescriptor() {
        return LensTestUtil.createProjectDescriptor(getLanguageLevel());
    }

    protected Project getProject() {
        return fixture.getProject();
    }

    protected TempDirTestFixture getTempDirFixture() {
        IdeaTestExecutionPolicy policy = IdeaTestExecutionPolicy.current();
        return policy != null
                ? policy.createTempDirTestFixture()
                : new LightTempDirTestFixtureImpl(true);
    }

    protected <T> T read(Callable<T> callable) throws Exception {
        CompletableFuture<T> future = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                T value = callable.call();
                future.complete(value);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        return future.get(1L, TimeUnit.SECONDS);
    }

    protected PsiClass findClass(String className) {
        Project project = fixture.getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        return facade.findClass(className, GlobalSearchScope.allScope(project));
    }
}
