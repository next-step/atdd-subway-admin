package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 엔티티 테스트")
public class StationTest {

	@Test
	void 생성() {
		//given

		//when
		Station 구로디지털단지 = new Station("구로디지털단지");

		//then
		assertThat(구로디지털단지).isNotNull();
	}

	@Test
	void 생성_역이름_null_예외발생() {
		//given

		//when

		//then
		assertThatThrownBy(() -> new Station(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_역이름_빈문자열_예외발생() {
		//given

		//when

		//then
		assertThatThrownBy(() -> new Station(""))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_역이름_255바이트_초과_예외발생() {
		//given

		//when
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//then
		assertThatThrownBy(
			() -> new Station(이백오십육바이트_이름))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 동일성() {
		//given
		Station 구로디지털단지 = new Station("구로디지털단지");
		Station 구로디지털단지_복제 = new Station("구로디지털단지");

		//when
		boolean 동일성_결과 = 구로디지털단지.equals(구로디지털단지_복제);

		//then
		assertThat(동일성_결과).isTrue();
	}

	@Test
	void 수정() {
		//given
		Station 구로디지털단지 = new Station("구로디지털단지");
		Station 홍대입구 = new Station("홍대입구");

		//when
		구로디지털단지.update(홍대입구);

		//then
		assertThat(구로디지털단지).isEqualTo(홍대입구);
	}
}
