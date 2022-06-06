package nextstep.subway.station.unit;

import static org.assertj.core.api.Assertions.assertThat;

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
		// when
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(gangnamStation, gwanggyoStaion);
	}

	@DisplayName("구간 사이에 새로운 구간 생성")
	@Test
	void createSectionInMiddle() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when
		sinbundangLine.addSection(gangnamStation, jeongjaStation, 10);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(gangnamStation, jeongjaStation, gwanggyoStaion);
	}

	@DisplayName("상행역 앞에 새로운 구간 생성")
	@Test
	void createSectionInFrontOfUpStation() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when
		sinbundangLine.addSection(jeongjaStation, gangnamStation, 10);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(jeongjaStation, gangnamStation, gwanggyoStaion);
	}

	@DisplayName("하행역 뒤에 새로운 구간 생성")
	@Test
	void createSectionBehindDownStation() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when
		sinbundangLine.addSection(gwanggyoStaion, jeongjaStation, 10);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(gangnamStation, gwanggyoStaion, jeongjaStation);
	}
}
