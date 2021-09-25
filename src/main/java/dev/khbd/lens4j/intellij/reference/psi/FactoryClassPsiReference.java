package dev.khbd.lens4j.intellij.reference.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;

import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class FactoryClassPsiReference extends PsiReferenceBase<PsiElement> {

    private final PsiClass psiClass;
    private final String factoryName;

    public FactoryClassPsiReference(PsiClass psiClass,
                                    PsiElement originalElement,
                                    String factoryName) {
        super(originalElement);
        this.psiClass = psiClass;
        this.factoryName = factoryName;
    }

    @Override
    public PsiElement resolve() {
        String packageName = PsiUtil.getPackageName(psiClass);
        if (Objects.isNull(packageName)) {
            return null;
        }

        Project project = getElement().getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);

        return facade.findClass(packageName + "." + factoryName, GlobalSearchScope.allScope(project));
    }
}
