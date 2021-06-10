package nextstep.subway.section.domain;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

class SectionsTest {

	@Test
	@DisplayName("Section 목록으로 부터 Station을 상행에서 하행순으로 정렬하는지 테스트")
	void orderedSections() {

		Sections sections = new Sections();

		Line line = new Line("2호선", "green");
		Station station1 = new Station("강남");
		Station station2 = new Station("역삼");
		Station station3 = new Station("선릉");
		Station station4 = new Station("종합운동장");
		Station station5 = new Station("잠실");

		Section section1 = new Section(line, station1, station2, 1);
		Section section2 = new Section(line, station2, station3, 1);
		Section section3 = new Section(line, station3, station4, 1);
		Section section4 = new Section(line, station4, station5, 1);

		sections.addSection(section1);
		sections.addSection(section3);
		sections.addSection(section2);
		sections.addSection(section4);

		List<Station> orderedStations = sections.getOrderedStations();
		Assertions.assertThat(orderedStations).containsExactly(station1, station2, station3, station4, station5);

	}
}