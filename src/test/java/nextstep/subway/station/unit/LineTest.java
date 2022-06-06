package nextstep.subway.station.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineTest {
	private Line sinbundangLine;
	private Station gangnamStation;
	private Station gwanggyoStaion;
	private Station jeongjaStation;

	@BeforeEach
	void init() {
		// given
		sinbundangLine = new Line("신분당선", "bg-red-600");
		gangnamStation = new Station("강남역");
		gwanggyoStaion = new Station("광교역");
		jeongjaStation = new Station("정자역");
	}

	@DisplayName("지하철에 구간 생성")
	@Test
	void createSection() {
		sinbundangLine.addSection(gangnamStation, jeongjaStation, 30);
	}
}
