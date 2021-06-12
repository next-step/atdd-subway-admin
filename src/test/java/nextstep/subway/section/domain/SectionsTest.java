package nextstep.subway.section.domain;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

class SectionsTest {

	private Line line;
	private Station 강남역;
	private Station 역삼역;
	private Station 선릉역;
	private Station 종합운동장역;
	private Station 잠실역;

	@BeforeEach
	void setup() {
		this.line = new Line("2호선", "green");
		this.강남역 = new Station("강남");
		this.역삼역 = new Station("역삼");
		this.선릉역 = new Station("선릉");
		this.종합운동장역 = new Station("종합운동장");
		this.잠실역 = new Station("잠실");
	}

	@Test
	@DisplayName("Section 목록으로 부터 Station을 상행에서 하행순으로 정렬하는지 테스트")
	void orderedSections() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.역삼역, 1);
		Section section2 = new Section(line, this.역삼역, this.선릉역, 1);
		Section section3 = new Section(line, this.선릉역, this.종합운동장역, 1);
		Section section4 = new Section(line, this.종합운동장역, this.잠실역, 1);

		sections.addSection(section1);
		sections.addSection(section2);
		sections.addSection(section3);
		sections.addSection(section4);

		List<Station> orderedStations = sections.getOrderedStations();
		Assertions.assertThat(orderedStations).containsExactly(강남역, 역삼역, 선릉역, 종합운동장역, 잠실역);
	}
}