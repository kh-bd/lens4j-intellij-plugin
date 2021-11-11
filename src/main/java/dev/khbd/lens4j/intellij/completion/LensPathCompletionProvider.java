package dev.khbd.lens4j.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.util.ProcessingContext;
import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathParser;
import dev.khbd.lens4j.common.PathPart;
import dev.khbd.lens4j.common.Property;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.common.Predicates;
import dev.khbd.lens4j.intellij.common.path.PathService;
import dev.khbd.lens4j.intellij.common.path.PsiMemberResolver;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        PathParser parser = PathParser.getInstance();
        Path path = parser.parse(pathStr);

        if (!PathService.getInstance().hasCorrectStructure(path)) {
            return;
        }

        PsiClass enclosingClass = LensPsiUtil.findFirstEnclosingClass(token).orElse(null);

        if (path.isEmpty()) {
            resultSet.addAllElements(allApplicableMembersAsVariants(enclosingClass));
            return;
        }

        if (isSingleProperty(path)) {
            resultSet.addAllElements(allApplicableMembersStartedWithAsVariants(enclosingClass, pathStr));
            return;
        }

        PsiMemberResolver resolver = new PsiMemberResolver(enclosingClass);
        path.visit(resolver);

        PathPart lastPart = path.getLastPart();

        // path is 'p1.'
        if (lastPart.isPoint() && resolver.isResolved()) {
            PsiType type = resolver.getLastResolvedType();
            LensPsiUtil.resolvePsiClassByType(type)
                    .ifPresent(psiClass -> resultSet.addAllElements(allApplicableMembersAsVariants(psiClass)));
            return;
        }

        // path is 'p1.p2' or 'p1.p2.prefix'
        if (lastPart.isProperty()) {
            Property property = (Property) lastPart;

            Path subPath = path.removeLastPart();
            PsiMemberResolver subPathResolver = new PsiMemberResolver(enclosingClass);
            subPath.visit(subPathResolver);

            if (subPathResolver.isResolved()) {
                resultSet.addAllElements(getApplicableMembersByType(subPathResolver.getLastResolvedType(), property.getName()));
            }
        }
    }

    private List<LookupElementBuilder> getApplicableMembersByType(PsiType type, String prefix) {
        PsiClass psiClass = LensPsiUtil.resolvePsiClassByType(type).orElse(null);
        if (Objects.isNull(psiClass)) {
            return List.of();
        }
        return allApplicableMembersStartedWithAsVariants(psiClass, prefix);
    }

    private List<LookupElementBuilder> allApplicableMembersAsVariants(PsiClass enclosingClass) {
        return Stream.concat(
                allFieldsAsVariants(enclosingClass),
                allMethodsAsVariants(enclosingClass)
        ).collect(Collectors.toList());
    }

    private Stream<LookupElementBuilder> allFieldsAsVariants(PsiClass enclosingClass) {
        return LensPsiUtil.findFields(enclosingClass, Predicates.isStatic(false))
                .stream()
                .map(this::toLookupBuilder);
    }

    private Stream<LookupElementBuilder> allMethodsAsVariants(PsiClass enclosingClass) {
        return LensPsiUtil.findMethods(enclosingClass, Predicates.isStatic(false), Predicates.APPLICABLE_METHOD)
                .stream()
                .map(this::toLookupBuilder);
    }

    private List<LookupElementBuilder> allApplicableMembersStartedWithAsVariants(PsiClass enclosingClass,
                                                                                 String prefix) {
        return Stream.concat(
                allFieldsStartsWithAsVariants(enclosingClass, prefix),
                allMethodsStartedWithAsVariants(enclosingClass, prefix)
        ).collect(Collectors.toList());
    }

    private Stream<LookupElementBuilder> allFieldsStartsWithAsVariants(PsiClass enclosingClass, String prefix) {
        return LensPsiUtil.findFields(enclosingClass, Predicates.isStatic(false), Predicates.nameStarts(prefix))
                .stream()
                .map(this::toLookupBuilder);
    }

    private Stream<LookupElementBuilder> allMethodsStartedWithAsVariants(PsiClass enclosingClass, String prefix) {
        return LensPsiUtil.findMethods(enclosingClass, Predicates.isStatic(false),
                        Predicates.APPLICABLE_METHOD, Predicates.nameStarts(prefix))
                .stream()
                .map(this::toLookupBuilder);
    }

    private LookupElementBuilder toLookupBuilder(PsiField field) {
        return LookupElementBuilder.createWithSmartPointer(field.getName(), field);
    }

    private LookupElementBuilder toLookupBuilder(PsiMethod method) {
        return LookupElementBuilder.createWithSmartPointer(method.getName() + "()", method);
    }

    private boolean isSingleProperty(Path path) {
        return path.length() == 1;
    }
}
