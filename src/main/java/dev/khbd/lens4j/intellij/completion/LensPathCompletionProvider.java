package dev.khbd.lens4j.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaToken;
import com.intellij.util.ProcessingContext;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.path.Path;
import dev.khbd.lens4j.intellij.common.path.PathParser;
import dev.khbd.lens4j.intellij.common.path.PathPart;
import dev.khbd.lens4j.intellij.common.path.Property;
import dev.khbd.lens4j.intellij.common.path.PsiFieldResolver;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(CompletionParameters parameters,
                                  ProcessingContext context,
                                  CompletionResultSet resultSet) {
        PsiJavaToken token = (PsiJavaToken) parameters.getOriginalPosition();
        String pathStr = token.getText().substring(1, token.getText().length() - 1);

        Path path = new PathParser().parse(pathStr);
        if (!path.hasCorrectStructure()) {
            return;
        }

        PsiClass enclosingClass = LensPsiUtil.findFirstEnclosingClass(token).orElse(null);

        if (path.isEmpty()) {
            resultSet.addAllElements(allFieldsAsVariants(enclosingClass));
            return;
        }

        if (isSingleProperty(path)) {
            resultSet.addAllElements(allFieldsStartsWithAsVariants(enclosingClass, pathStr));
            return;
        }

        PsiFieldResolver resolver = new PsiFieldResolver(enclosingClass);
        path.visit(resolver);
        resolver.getResolvedField();

        PathPart lastPart = path.getLastPart();

        // path is 'p1.p2' or 'p1.pr2.prefix'
        if (lastPart.isProperty()) {
            Property property = (Property) lastPart;
            if (resolver.isResolved()) {
                // path is correct and fully resolved, but can exist more variants
                PsiClass propertyClass = resolver.getResolvedField().getContainingClass();
                resultSet.addAllElements(allFieldsStartsWithAsVariants(propertyClass, property.getProperty()));
                return;
            }

            if (isResolved(path.removeLastPart(), enclosingClass)) {
                resultSet.addAllElements(allFieldsStartsWithAsVariants(resolver.getResolvedClass(), property.getProperty()));
                return;
            }
        }

        // path is 'p1.p2.'
        if (lastPart.isPoint() && resolver.isResolved()) {
            resultSet.addAllElements(allFieldsAsVariants(resolver.getResolvedClass()));
        }
    }

    private boolean isResolved(Path path, PsiClass rootClass) {
        PsiFieldResolver resolver = new PsiFieldResolver(rootClass);
        path.visit(resolver);
        return resolver.isResolved();
    }

    private List<LookupElementBuilder> allFieldsAsVariants(PsiClass enclosingClass) {
        return LensPsiUtil.findAllFields(enclosingClass, false)
                .stream()
                .map(LookupElementBuilder::create)
                .collect(Collectors.toList());

    }

    private List<LookupElementBuilder> allFieldsStartsWithAsVariants(PsiClass enclosingClass, String prefix) {
        return LensPsiUtil.findAllFields(enclosingClass, false)
                .stream()
                .filter(field -> field.getName().startsWith(prefix))
                .map(LookupElementBuilder::create)
                .collect(Collectors.toList());
    }

    private boolean isSingleProperty(Path path) {
        return path.length() == 1;
    }
}
