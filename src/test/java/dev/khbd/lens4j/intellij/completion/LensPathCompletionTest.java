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
    public void complete_existsDuplicateMethods_deduplicateThem() {
        fixture.configureByFiles("completion/duplicate_methods/Payment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements)
                .containsExactlyInAnyOrder("amount",
                        "getAmount()", "clone()", "hashCode()",
                        "toString()", "getClass()",
                        "sum()"
                );
    }

    @Test
    public void complete_pathIsEmpty_listAllClassFieldsAndMethods() {
        fixture.configureByFiles("completion/empty_path/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements)
                .containsExactlyInAnyOrder("from", "to", "amount",
                        "getAmount()", "clone()", "hashCode()",
                        "toString()", "getClass()",
                        "getFrom()"
                );
    }

    // single property path

    @Test
    public void completeSingle_noOnePropertyAndMethodFoundWithPrefix_listEmpty() {
        fixture.configureByFiles("completion/single/not_exist/Payment.java", "completion/BasePayment.java");
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
    public void completeSingle_exactOneMethodFoundWithPrefix_autoComplete() {
        fixture.configureByFiles("completion/single/exists_one_method/Payment.java", "completion/BasePayment.java");
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

    @Test
    public void completeSingle_moreThanOneMethodFoundWithPrefix_listAll() {
        fixture.configureByFiles("completion/single/exist_more_than_one_methods/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("getAmount()", "getClass()", "getFrom()");
    }

    @Test
    public void completeSingle_existPropertiesAndMethodsTogether_listAll() {
        fixture.configureByFiles("completion/single/exist_properties_and_methods/Payment.java", "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("amount()", "amount");
    }

    // ends with point

    @Test
    public void complete_pathEndsWithPointAndPathIsValidAndPropertyBeforePoint_listAllMembers() {
        fixture.configureByFiles("completion/ends_with_point/path_valid/property_before_point/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("accountNumber", "bic",
                "clone()", "hashCode()",
                "toString()", "getClass()",
                "getAccountNumber()", "getBic()"
        );
    }

    @Test
    public void complete_pathEndsWithPointAndPathIsValidAndMethodBeforePoint_listAllMembers() {
        fixture.configureByFiles("completion/ends_with_point/path_valid/method_before_point/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("accountNumber", "bic",
                "clone()", "hashCode()",
                "toString()", "getClass()",
                "getAccountNumber()", "getBic()"
        );
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
    public void complete_pathIsResolvedAndPropertyIsMethod_autoComplete() {
        fixture.configureByFiles("completion/ends_with_property/path_is_resolved/property_is_method/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).isNull();
    }

    @Test
    public void complete_pathIsResolvedAndMoreMembersExists_listAllOfThem() {
        fixture.configureByFiles("completion/ends_with_property/path_is_resolved/more_members_exist/Payment.java", "completion/BasePayment.java", "completion/Account.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("code", "code1", "code2", "codeM()");
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
    public void complete_pathIsNotResolvedAndRemainedPathIsResolvedAndExistsMoreThanOneMembers_listAllOfThem() {
        fixture.configureByFiles("completion/ends_with_property/path_is_not_resolved/remain_path_is_resolved/exists_members_with_prefix/Payment.java",
                "completion/BasePayment.java");
        fixture.complete(CompletionType.BASIC);

        List<String> lookupElements = fixture.getLookupElementStrings();

        assertThat(lookupElements).containsExactlyInAnyOrder("value", "valueStr", "valueFull", "valueStrMethod()");
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
    public void complete_pathIsNotResolvedAndRemainedPathIsResolvedAndExistsOneMethod_autoComplete() {
        fixture.configureByFiles("completion/ends_with_property/path_is_not_resolved/remain_path_is_resolved/exists_one_method_with_prefix/Payment.java",
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