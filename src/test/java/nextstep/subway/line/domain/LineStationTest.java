package nextstep.subway.line.domain;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 역")
class LineStationTest {
	@DisplayName("노선 역을 생성한다.")
	@Test
	void of() {
		// given & when
		LineStation lineStation = LineStation.of(강남역(), 역삼역(), 4);

		// then
		assertThat(lineStation).isNotNull();
	}

	@DisplayName("현재 역과 이전 역이 같은 경우 노선 역을 생성할 수 없다.")
	@Test
	void ofFailIfStationAndPreStationIsEqual() {
		// given & when & then
		assertThatThrownBy(() -> LineStation.of(강남역(), 강남역(), 0))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
