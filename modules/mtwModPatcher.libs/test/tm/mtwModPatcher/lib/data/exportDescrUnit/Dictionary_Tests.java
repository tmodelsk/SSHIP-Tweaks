package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Dictionary_Tests {

	@Test
	public void  parse_value() {
		val eduStr = "baghlah";

		val dict = Dictionary.parse(eduStr);

		assertThat(dict).isNotNull();
		assertThat(dict.value).isEqualTo(eduStr);
		assertThat(dict.comment).isNull();
	}

	@Test
	public void  parse_value_comment() {
		val eduStr = "baghlah      ; Baghlah";

		val dict = Dictionary.parse(eduStr);

		assertThat(dict).isNotNull();
		assertThat(dict.value).isEqualTo("baghlah");
		assertThat(dict.comment).isEqualTo("      ; Baghlah");
	}

	@Test
	public void  parse_valueWithUnderscore_comment() {
		val eduStr = "Polish_Retainers      ; Polish Retainers, feudal, medium cavalry, average";

		val dict = Dictionary.parse(eduStr);

		assertThat(dict).isNotNull();
		assertThat(dict.value).isEqualTo("Polish_Retainers");
		assertThat(dict.comment).isEqualTo("      ; Polish Retainers, feudal, medium cavalry, average");
	}

	@Test
	public void  toEduString_value_comment() {
		val valueStr = "baghlah";
		val dict = new Dictionary(valueStr, "      ; Baghlah");

		val eduStr = dict.toEduString();

		assertThat(eduStr).isEqualTo("baghlah      ; Baghlah");
	}

	@Test
	public void  toEduString_value() {
		val valueStr = "baghlah";
		val dict = new Dictionary(valueStr, null);

		val eduStr = dict.toEduString();

		assertThat(eduStr).isEqualTo(valueStr);
	}

}
