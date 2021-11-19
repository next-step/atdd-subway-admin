package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

class SectionsTest {

	@DisplayName("상행종착구간 찾기")
	@Test
	void getUpTerminalSection() {
		final Station 강남역 = Station.of("강남역");
		final Station 판교역 = Station.of("판교역");
		final Station 광교역 = Station.of("광교역");
		final Line line = Line.of("신분당선", "red");

		final Sections sections = new Sections();
		final Section 상행종착구간 = Section.of(line, 강남역, 판교역, 0);
		sections.add(상행종착구간);
		final Section 하행종착구간 = Section.of(line, 판교역, 광교역, 0);
		sections.add(하행종착구간);

		assertThat(sections.getUpTerminalSection()).isSameAs(상행종착구간);
	}

	@DisplayName("상행종착구간을 찾지 못하면 SectionNotFoundException 발생")
	@Test
	void getUpTerminalSection_notFoundSection() {
		final Sections sections = new Sections();
		assertThatExceptionOfType(SectionNotFoundException.class)
			.isThrownBy(sections::getUpTerminalSection)
			.withMessage(SectionNotFoundException.MESSAGE);
	}

	@DisplayName("특정 구간에서부터 상행->하행 순으로 정렬된 모든 역의 목록 조회")
	@Test
	void getAllStationSortedByUpToDownFrom() {
		final Station 강남역 = Station.of("강남역");
		final Station 양재역 = Station.of("양재역");
		final Station 판교역 = Station.of("판교역");
		final Station 광교역 = Station.of("광교역");
		final Line line = Line.of("신분당선", "red");

		final Sections sections = new Sections();
		sections.add(Section.of(line, 강남역, 양재역, 0));
		final Section firstSection = Section.of(line, 양재역, 판교역, 0);
		sections.add(firstSection);
		sections.add(Section.of(line, 판교역, 광교역, 0));

		assertThat(sections.getAllStationSortedByUpToDownFrom(firstSection))
			.containsExactly(양재역, 판교역, 광교역);
	}
}
