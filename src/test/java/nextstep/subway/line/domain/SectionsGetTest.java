package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

public class SectionsGetTest {

	private final Station 강남역 = Station.of("강남역");
	private final Station 양재역 = Station.of("양재역");
	private final Station 판교역 = Station.of("판교역");
	private final Station 광교역 = Station.of("광교역");
	private final Line 신분당선 = Line.of("신분당선", "red");

	@DisplayName("상행종착구간 찾기")
	@Test
	void getUpTerminalSection() {
		final Sections sections = new Sections();
		final Section 상행종착구간 = Section.of(신분당선, 강남역, 판교역, 10);
		sections.add(상행종착구간);
		sections.add(Section.of(신분당선, 판교역, 광교역, 10));
		assertThat(sections.getUpTerminalSection()).isSameAs(상행종착구간);
	}

	@DisplayName("상행종착구간을 찾지 못하면 SectionNotFoundException 발생")
	@Test
	void getUpTerminalSection_not_found_section() {
		final Sections sections = new Sections();
		assertThatExceptionOfType(SectionNotFoundException.class)
			.isThrownBy(sections::getUpTerminalSection)
			.withMessage(SectionNotFoundException.MESSAGE);
	}

	@DisplayName("특정 구간에서부터 상행->하행 순으로 정렬된 모든 역의 목록 조회")
	@Test
	void getAllStationSortedByUpToDownFrom() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 양재역, 10));
		final Section firstSection = Section.of(신분당선, 양재역, 판교역, 10);
		sections.add(firstSection);
		sections.add(Section.of(신분당선, 판교역, 광교역, 10));

		assertThat(sections.getAllStationSortedByUpToDownFrom(firstSection))
			.containsExactly(양재역, 판교역, 광교역);
	}
}
