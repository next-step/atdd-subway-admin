package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DisplayName("섹션 엔티티 테스트")
public class SectionTest {

	@Test
	void 생성() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Station 상행역 = new Station("상행역");
		Station 하행역 = new Station("하행역");
		double 거리 = 0.01D;

		//when
		Section 섹션 = new Section(이호선, 상행역, 하행역, 거리);

		//then
		assertThat(섹션).isNotNull();
	}

	@Test
	void 생성_노선없음_예외발생() {
		//given
		Station 상행역 = new Station("상행역");
		Station 하행역 = new Station("하행역");
		double 거리 = 0.01D;

		//when

		//then
		assertThatThrownBy(() -> new Section(null, 상행역, 하행역, 거리))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_상행역없음_예외발생() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Station 하행역 = new Station("하행역");
		double 거리 = 0.01D;

		//when

		//then
		assertThatThrownBy(() -> new Section(이호선, null, 하행역, 거리))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_하행역없음_예외발생() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Station 상행역 = new Station("상행역");
		double 거리 = 0.01D;

		//when

		//then
		assertThatThrownBy(() -> new Section(이호선, 상행역, null, 거리))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_거리_0이하_예외발생() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Station 구로디지털단지 = new Station("구로디지털단지");
		Station 홍대입구 = new Station("홍대입구");
		double 거리_0이하 = 0.00D;

		//when

		//then
		assertThatThrownBy(() -> new Section(이호선, 구로디지털단지, 홍대입구, 거리_0이하))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 생성_상행역_하행역_같음_예외발생() {
		//given
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Station 홍대입구 = new Station("홍대입구");
		double 거리_0이하 = 0.00D;

		//when

		//then
		assertThatThrownBy(() -> new Section(이호선, 홍대입구, 홍대입구, 거리_0이하))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
