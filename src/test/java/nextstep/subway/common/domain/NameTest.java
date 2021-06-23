package nextstep.subway.common.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 역 또는 노선 이름 테스트")
public class NameTest {

	@Test
	void 생성() {
		//given
		String text = "홍대입구";

		//when
		Name 이름 = Name.generate(text);

		//then
		assertThat(이름).isNotNull();
	}

	@Test
	void 생성_null_예외발생() {
		//given
		String null값 = null;

		//when

		//then
		assertThatThrownBy(() -> Name.generate(null값)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_빈문자열_예외발생() {
		//given
		String 빈_문자열 = "";

		//when

		//then
		assertThatThrownBy(() -> Name.generate(빈_문자열)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//when

		//then
		assertThatThrownBy(() -> Name.generate(이백오십육바이트_이름))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 변경() {
		//given
		Name 이름 = Name.generate("홍대입구");
		String 변경할_이름 = "구로디지털단지";

		//when
		이름.changeName(변경할_이름);

		//then
		assertThat(이름.value()).isEqualTo(변경할_이름);
	}

	@Test
	void 변경_null_예외발생() {
		//given
		Name 이름 = Name.generate("홍대입구");
		String null값 = null;

		//when

		//then
		assertThatThrownBy(() -> 이름.changeName(null값)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 변경_빈문자열_예외발생() {
		//given
		Name 이름 = Name.generate("홍대입구");
		String 빈_문자열 = "";

		//when

		//then
		assertThatThrownBy(() -> 이름.changeName(빈_문자열)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 변경_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//when

		//then
		assertThatThrownBy(() -> Name.generate(이백오십육바이트_이름))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 동일성() {
		//given
		Name 이름 = Name.generate("구로디지털단지");
		Name 비교할_이름 = Name.generate("구로디지털단지");

		//when
		boolean 동일성여부 = 이름.equals(비교할_이름);

		//then
		assertThat(동일성여부).isTrue();
	}
}
