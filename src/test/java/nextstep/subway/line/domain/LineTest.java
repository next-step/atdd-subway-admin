package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class LineTest {
	@Test
	@DisplayName("노선 생성 시, 이전역과 거리가 없는 시작 구간과 이전역이 시작종점이고 거리가 입력받은 거리인 노선이 생성되어야한다")
	void createLineWithStations() {
		Station upStation = new Station("문래역");
		Station downStation = new Station("잠실역");

		Line line = new Line("2호선", "green", upStation, downStation, 10);

		assertThat(line.getStations().get(0).getName()).isEqualTo("문래역");
		assertThat(line.getStations().get(1).getName()).isEqualTo("잠실역");
		assertThat(line.getSections().get(0).getPreStation()).isNull();
		assertThat(line.getSections().get(0).getDistance()).isEqualTo(0);
		assertThat(line.getSections().get(1).getPreStation()).isEqualTo(upStation);
		assertThat(line.getSections().get(1).getDistance()).isEqualTo(10);
	}
}
