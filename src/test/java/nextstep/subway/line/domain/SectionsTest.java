package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

class SectionsTest {

	@Test
	void 섹션리스트에서_정거장리스트를_반환하는지_테스트() {
		// given
		Line line = new Line("2호선", "green");
		Station upStation1 = new Station("신도림역");
		Station upStation2 = new Station("왕십리역");
		Station downStation1 = new Station("성수역");
		Station downStation2 = new Station("을지로입구역");
		Section section1 = new Section(line, upStation1, downStation1, 15);
		Section section2 = new Section(line, upStation2, downStation2, 10);

		Sections sections = new Sections(Arrays.asList(section1, section2));

		// when
		List<StationResponse> stationList = sections.getStations();

		// then
		assertThat(stationList).hasSize(4);
	}
}