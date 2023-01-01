package dev.khbd.lens4j.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
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
import lombok.RequiredArgsConstructor;

import java.util.Collections;
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
            resultSet.addAllElements(getClassVariantSource(enclosingClass).getVariants());
            return;
        }

        if (isSingleProperty(path)) {
            resultSet.addAllElements(getClassVariantSource(enclosingClass).prefixed(pathStr).getVariants());
            return;
        }

        PsiMemberResolver resolver = new PsiMemberResolver(enclosingClass);
        path.visit(resolver);

        PathPart lastPart = path.getLastPart();

        // path is 'p1.'
        if (lastPart.isPoint() && resolver.isResolved()) {
            PsiType type = resolver.getLastResolvedType();
            resultSet.addAllElements(getVariantSourceByType(type).getVariants());
            return;
        }

        // path is 'p1.p2' or 'p1.p2.prefix'
        if (lastPart.isProperty()) {
            Property property = (Property) lastPart;

            Path subPath = path.removeLastPart();
            PsiMemberResolver subPathResolver = new PsiMemberResolver(enclosingClass);
            subPath.visit(subPathResolver);

            if (subPathResolver.isResolved()) {
                VariantSource source = getVariantSourceByType(subPathResolver.getLastResolvedType())
                        .prefixed(property.getName());
                resultSet.addAllElements(source.getVariants());
            }
        }
    }

    private boolean isSingleProperty(Path path) {
        return path.length() == 1;
    }

    private VariantSource getClassVariantSource(PsiClass psiClass) {
        return new ClassFieldsVariantSource(psiClass)
                .with(new ClassMethodsVariantSource(psiClass));
    }

    private VariantSource getVariantSourceByType(PsiType type) {
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            PsiClass psiClass = classType.resolve();
            if (Objects.isNull(psiClass)) {
                return new EmptyVariantSource();
            }
            return getClassVariantSource(psiClass);
        }
        if (type instanceof PsiArrayType) {
            return new ArrayVariantSource();
        }
        return new EmptyVariantSource();
    }

    static abstract class VariantSource {

        abstract List<LookupElementBuilder> getVariants();

        VariantSource prefixed(String prefix) {
            return new VariantSource() {
                @Override
                List<LookupElementBuilder> getVariants() {
                    return VariantSource.this.getVariants().stream()
                            .filter(variant -> variant.getLookupString().startsWith(prefix))
                            .collect(Collectors.toList());
                }
            };
        }

        VariantSource with(VariantSource other) {
            return new VariantSource() {
                @Override
                List<LookupElementBuilder> getVariants() {
                    return Stream.concat(VariantSource.this.getVariants().stream(),
                            other.getVariants().stream()
                    ).collect(Collectors.toList());
                }
            };
        }
    }

    @RequiredArgsConstructor
    static class ClassFieldsVariantSource extends VariantSource {

        private final PsiClass psiClass;

        @Override
        public List<LookupElementBuilder> getVariants() {
            return LensPsiUtil.findFields(psiClass, Predicates.isStatic(false))
                    .stream()
                    .map(this::toLookupBuilder)
                    .collect(Collectors.toList());
        }

        private LookupElementBuilder toLookupBuilder(PsiField field) {
            return LookupElementBuilder.createWithSmartPointer(field.getName(), field);
        }
    }

    @RequiredArgsConstructor
    static class ClassMethodsVariantSource extends VariantSource {

        private final PsiClass psiClass;

        @Override
        public List<LookupElementBuilder> getVariants() {
            return LensPsiUtil.findMethods(psiClass, Predicates.isStatic(false),
                            Predicates.APPLICABLE_METHOD)
                    .stream()
                    .map(this::toLookupBuilder)
                    .collect(Collectors.toList());
        }

        private LookupElementBuilder toLookupBuilder(PsiMethod method) {
            return LookupElementBuilder.createWithSmartPointer(method.getName() + "()", method);
        }
    }

    static class ArrayVariantSource extends VariantSource {
        @Override
        public List<LookupElementBuilder> getVariants() {
            return List.of(LookupElementBuilder.create("length"));
        }
    }

    static class EmptyVariantSource extends VariantSource {

        @Override
        public List<LookupElementBuilder> getVariants() {
            return Collections.emptyList();
        }
    }

}
