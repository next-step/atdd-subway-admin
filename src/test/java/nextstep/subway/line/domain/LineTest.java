package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationGroup;

@DisplayName("노선 엔티티 테스트")
public class LineTest {

	@Test
	void 생성() {
		//given

		//when
		Line 이호선 = new Line("2호선", "#FFFFFF");

		//then
		assertThat(이호선).isNotNull();
	}

	@Test
	void 생성_노선이름_null_예외발생() {
		//given

		//when

		//then
		assertThatThrownBy(() -> new Line(null, "#FFFFFF"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_노선이름_빈문자열_예외발생() {
		//given

		//when

		//then
		assertThatThrownBy(() -> new Line("", "#FFFFFF"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_노선이름_255바이트_초과_예외발생() {
		//given

		//when
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//then
		assertThatThrownBy(
			() -> new Line(이백오십육바이트_이름, "#FFFFFF"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_색상_null_예외발생() {
		//given

		//when

		//then
		assertThatThrownBy(() -> new Line("2호선", null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_색상_빈문자열_예외발생() {
		//given

		//when

		//then
		assertThatThrownBy(() -> new Line("2호선", ""))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_색상_255바이트_초과_예외발생() {
		//given

		//when
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";

		//then
		assertThatThrownBy(
			() -> new Line("2호선", 이백오십육바이트_색상))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_역_그룹_포함() {
		//given
		Station 구로디지털단지 = new Station("구로디지털단지");
		Station 홍대입구 = new Station("홍대입구");
		StationGroup 역_그룹 = new StationGroup(Arrays.asList(구로디지털단지, 홍대입구));

		//when
		Line 이호선 = new Line("2호선", "#FFFFFF", 역_그룹);

		//then
		assertThat(이호선).isNotNull();
		assertThat(이호선.stations()).containsSequence(역_그룹.stations());
	}

	@Test
	void 동일성() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Line 이호선_복제 = new Line("2호선", "#FFFFFF");

		//when
		boolean 동일성_결과 = 이호선.equals(이호선_복제);

		//then
		assertThat(동일성_결과).isTrue();
	}

	@Test
	void 수정() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Line 사호선 = new Line("4호선", "#666666");

		//when
		이호선.update(사호선);

		//then
		assertThat(이호선).isEqualTo(사호선);
	}
}
