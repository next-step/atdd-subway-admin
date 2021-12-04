package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

public class SectionsTest {
	@Test
	void getStations() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");
		Station station4 = new Station("강남역");

		Section section1 = new Section(station3, station2, 5);
		Section section2 = new Section(station1, station3, 7);
		Section section3 = new Section(station2, station4, 10);

		Sections sections = new Sections(Arrays.asList(section3, section1, section2));

		assertThat(sections.getStationsList()).isEqualTo(Arrays.asList(station1, station3, station2, station4));
	}
}
