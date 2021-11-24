package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.DuplicatedSectionException;
import nextstep.subway.line.exception.IllegalSectionException;
import nextstep.subway.station.domain.Station;

class SectionsAddTest {

	private final Station 강남역 = Station.of("강남역");
	private final Station 양재역 = Station.of("양재역");
	private final Station 판교역 = Station.of("판교역");
	private final Station 광교역 = Station.of("광교역");
	private final Line 신분당선 = Line.of("신분당선", "red");

	@DisplayName("구간추가/상행종착/1번만")
	@Test
	void add_upTerminal_once() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 양재역, 판교역, 10));

		final Section firstSection = Section.of(신분당선, 강남역, 양재역, 10);
		sections.add(firstSection);

		assertThat(sections.getAllStationSortedByUpToDownFrom(firstSection))
			.containsExactly(강남역, 양재역, 판교역);
	}

	@DisplayName("구간추가/상행종착/2번연속")
	@Test
	void add_upTerminal_twice() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 판교역, 광교역, 4));

		sections.add(Section.of(신분당선, 양재역, 판교역, 10));
		final Section upTerminalSection = Section.of(신분당선, 강남역, 양재역, 4);
		sections.add(upTerminalSection);

		assertThat(sections.getAllStationSortedByUpToDownFrom(upTerminalSection))
			.containsExactly(강남역, 양재역, 판교역, 광교역);
	}

	@DisplayName("구간추가/하행종착")
	@Test
	void add_downTerminal() {
		final Sections sections = new Sections();
		final Section firstSection = Section.of(신분당선, 강남역, 양재역, 10);
		sections.add(firstSection);

		sections.add(Section.of(신분당선, 양재역, 판교역, 10));

		assertThat(sections.getAllStationSortedByUpToDownFrom(firstSection))
			.containsExactly(강남역, 양재역, 판교역);
	}

	@DisplayName("구간추가/구간사이/동일상행역")
	@Test
	void add_between_same_upStation() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 광교역, 10));

		final Section firstSection = Section.of(신분당선, 강남역, 양재역, 3);
		sections.add(firstSection);

		assertThat(sections.getAllStationSortedByUpToDownFrom(firstSection))
			.containsExactly(강남역, 양재역, 광교역);
	}

	@DisplayName("구간추가/구간사이/동일하행역")
	@Test
	void add_between_same_downStation() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 광교역, 10));
		sections.add(Section.of(신분당선, 판교역, 광교역, 4));

		final Section upTerminalSection = sections.getUpTerminalSection();
		assertThat(upTerminalSection.getDownStation()).isEqualTo(판교역);
		assertThat(upTerminalSection.getDistance()).isEqualTo(6);
		assertThat(sections.getAllStationSortedByUpToDownFrom(upTerminalSection))
			.containsExactly(강남역, 판교역, 광교역);
	}

	@DisplayName("구간추가/중복이면 실패")
	@Test
	void add_duplicated_section() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 광교역, 10));
		assertThatExceptionOfType(DuplicatedSectionException.class)
			.isThrownBy(() -> sections.add(Section.of(신분당선, 강남역, 광교역, 10)))
			.withMessage(DuplicatedSectionException.MESSAGE);
	}

	@DisplayName("구간추가/노선에 포함되지 않은 역으로만 구성되어 실패")
	@Test
	void add_not_found_stations_in_line() {
		final Station 신분당선아닌역1 = Station.of("서울역");
		final Station 신분당선아닌역2 = Station.of("신도림역");

		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 양재역, 10));

		assertThatExceptionOfType(IllegalSectionException.class)
			.isThrownBy(() -> sections.add(Section.of(신분당선, 신분당선아닌역1, 신분당선아닌역2, 10)))
			.withMessage(IllegalSectionException.MESSAGE);
	}

	@DisplayName("구간추가/상행역과 하행역이 같으면 실패")
	@Test
	void add_same_upStation_downStation() {
		final Sections sections = new Sections();
		assertThatExceptionOfType(IllegalSectionException.class)
			.isThrownBy(() -> sections.add(Section.of(신분당선, 강남역, 강남역, 10)))
			.withMessage(IllegalSectionException.MESSAGE);
	}
}
