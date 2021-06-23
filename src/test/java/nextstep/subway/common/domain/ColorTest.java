package nextstep.subway.common.domain;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("색상 테스트")
public class ColorTest {

	@Test
	void 생성() {
		//given
		String text = "#FFFFFF";

		//when
		Color 색상 = Color.generate(text);

		//then
		assertThat(색상).isNotNull();
	}

	@Test
	void 생성_null_예외발생() {
		//given
		String null값 = null;

		//when

		//then
		assertThatThrownBy(() -> Color.generate(null값)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_빈문자열_예외발생() {
		//given
		String 빈_문자열 = "";

		//when

		//then
		assertThatThrownBy(() -> Color.generate(빈_문자열)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";
		System.out.println(이백오십육바이트_색상.getBytes(StandardCharsets.UTF_8).length);

		//when

		//then
		assertThatThrownBy(() -> Color.generate(이백오십육바이트_색상))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 변경() {
		//given
		Color 색상 = Color.generate("#FFFFFF");
		String 변경할_색상 = "#000000";

		//when
		색상.changeColor(변경할_색상);

		//then
		assertThat(색상.value()).isEqualTo(변경할_색상);
	}

	@Test
	void 변경_null_예외발생() {
		//given
		Color 색상 = Color.generate("#FFFFFF");
		String null값 = null;

		//when

		//then
		assertThatThrownBy(() -> 색상.changeColor(null값)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 변경_빈문자열_예외발생() {
		//given
		Color 색상 = Color.generate("#FFFFFF");
		String 빈_문자열 = "";

		//when

		//then
		assertThatThrownBy(() -> 색상.changeColor(빈_문자열)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 변경_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";

		//when

		//then
		assertThatThrownBy(() -> Color.generate(이백오십육바이트_색상))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 동일성() {
		//given
		Color 색상 = Color.generate("#FFFFFF");
		Color 비교할_색상 = Color.generate("#FFFFFF");

		//when
		boolean 동일성여부 = 색상.equals(비교할_색상);

		//then
		assertThat(동일성여부).isTrue();
	}
}
