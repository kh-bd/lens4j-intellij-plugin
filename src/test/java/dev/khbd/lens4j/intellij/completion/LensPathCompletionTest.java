package dev.khbd.lens4j.intellij.completion;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.completion.CompletionType;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathCompletionTest extends BaseIntellijTest {

    @Test
    public void complete_pathIsEmpty_listAllClassFields() {
        fixture.configureByFiles("completion/empty_path/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements)
                .containsExactlyInAnyOrder("from", "to", "amount");
    }

    // single property path

    @Test
    public void completeSingle_noOnePropertyFoundWithPrefix_listEmpty() {
        fixture.configureByFiles("completion/single/not_exist_property/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isEmpty();
    }

    @Test
    public void completeSingle_exactOnePropertyFoundWithPrefix_autoComplete() {
        fixture.configureByFiles("completion/single/exists_one_property/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isNull();
    }

    @Test
    public void completeSingle_moreThanOnePropertyFoundWithPrefix_listAll() {
        fixture.configureByFiles("completion/single/exist_more_than_one_properties/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("from", "fromChild");
    }

    // ends with point

    @Test
    public void complete_pathEndsWithPointAndPathIsValid_listAllFields() {
        fixture.configureByFiles("completion/ends_with_point/path_valid/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("accountNumber", "bic");
    }

    @Test
    public void complete_pathEndsWithPointAndPathNotValid_listEmpty() {
        fixture.configureByFiles("completion/ends_with_point/path_not_valid/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isEmpty();
    }

    // ends with property

    @Test
    public void complete_pathIsResolvedAndNoMorePropertiesExists_autoComplete() {
        fixture.configureByFiles("completion/ends_with_property/path_is_resolved/no_more_properties_exist/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isNull();
    }

    @Test
    public void complete_pathIsResolvedAndMorePropertiesExists_listAllOfThem() {
        fixture.configureByFiles("completion/ends_with_property/path_is_resolved/more_properties_exist/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("code", "code1", "code2");
    }

    @Test
    public void complete_pathIsNotResolvedAndRemainedPathIsNotResolved_emptyList() {
        fixture.configureByFiles("completion/ends_with_property/path_is_not_resolved/remain_path_is_not_resolved/Payment.java",
                "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isEmpty();
    }

    @Test
    public void complete_pathIsNotResolvedAndRemainedPathIsResolvedAndExistsMoreThanOneFields_listAllOfThem() {
        fixture.configureByFiles("completion/ends_with_property/path_is_not_resolved/remain_path_is_resolved/exists_fields_with_prefix/Payment.java",
                "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("value", "valueStr", "valueFull");
    }

    @Test
    public void complete_pathIsNotResolvedAndRemainedPathIsResolvedAndExistsOneField_autoComplete() {
        fixture.configureByFiles("completion/ends_with_property/path_is_not_resolved/remain_path_is_resolved/exists_one_field_with_prefix/Payment.java",
                "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isNull();
    }

    @Test
    public void complete_pathIsNotResolvedAndRemainedPathIsResolvedAndNoOneFieldExist_returnEmpty() {
        fixture.configureByFiles("completion/ends_with_property/path_is_not_resolved/remain_path_is_resolved/no_one_field_exist/Payment.java",
                "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isEmpty();
    }

    // wrong structure

    @Test
    public void complete_pathStartsFromPoint_returnEmpty() {
        fixture.configureByFiles("completion/wrong_structure/starts_from_point/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isEmpty();
    }
}