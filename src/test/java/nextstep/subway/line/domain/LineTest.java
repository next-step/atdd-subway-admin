package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class LineTest {
	static Line 신분당선 = new Line("신분당선", "빨강");

	@Test
	@DisplayName("노선은 종점역 정보를 구간의 형태로 관리한다")
	void addSectionTest() {
		// given
		Line line = Line.of(강남, 양재, "신분당선", "빨강", 10);

		// when
		Sections sections = line.getSections();

		// then
		List<Station> stations = sections.orderedStations();
		assertThat(stations.get(0)).isEqualTo(강남);
		assertThat(stations.get(1)).isEqualTo(양재);
	}
}
