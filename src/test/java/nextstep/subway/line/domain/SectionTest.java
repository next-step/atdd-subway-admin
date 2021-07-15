package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.InvalidDistanceException;
import nextstep.subway.station.domain.Station;

class SectionTest {

	Section section;
	Station 신도림역;
	Station 잠실역;

	@BeforeEach
	void setup() {
		신도림역 = new Station("신도림역");
		잠실역 = new Station("잠실역");

		Line line = new Line("2호선", "green", 신도림역, 잠실역, 10);
		section = new Section(line, 신도림역, 잠실역, 10);
	}

	@DisplayName("구간에 포함된 역들을 반환한다")
	@Test
	void 구간에_포함된_역들을_반환한다() {
		assertThat(section.getStations()).contains(신도림역, 잠실역);
	}

	@DisplayName("상행역을 업데이트한다.")
	@Test
	void 상행역을_업데이트한다() {
		Station 서울대입구역 = new Station("서울대입구역");
		section.updateUpStation(서울대입구역, 5);
		assertThat(section.getUpStation()).isEqualTo(서울대입구역);
		assertThat(section.getDistance()).isEqualTo(5);
	}

	@DisplayName("상행역을 업데이트 중 구간 거리보다 긴 거리가 들어온 경우, 에러가 발생한다.")
	@Test
	void 상행역을_업데이트_중_에러() {
		Station 서울대입구역 = new Station("서울대입구역");
		assertThrows(InvalidDistanceException.class, () -> {
			section.updateUpStation(서울대입구역, 15);
		});
	}

	@DisplayName("하행역을 업데이트한다.")
	@Test
	void 하행역을_업데이트한다() {
		Station 서울대입구역 = new Station("서울대입구역");
		section.updateDownStation(서울대입구역, 5);
		assertThat(section.getDownStation()).isEqualTo(서울대입구역);
		assertThat(section.getDistance()).isEqualTo(5);
	}

	@DisplayName("하행역을 업데이트 중 구간 거리보다 긴 거리가 들어온 경우, 에러가 발생한다.")
	@Test
	void 하행역을_업데이트_중_에러() {
		Station 서울대입구역 = new Station("서울대입구역");
		assertThrows(InvalidDistanceException.class, () -> {
			section.updateDownStation(서울대입구역, 15);
		});
	}

}
