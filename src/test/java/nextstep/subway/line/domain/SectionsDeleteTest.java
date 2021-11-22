package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.DeletableSectionNotFoundException;
import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

public class SectionsDeleteTest {

	private final Station 강남역 = Station.of("강남역");
	private final Station 양재역 = Station.of("양재역");
	private final Station 판교역 = Station.of("판교역");
	private final Station 광교역 = Station.of("광교역");
	private final Line 신분당선 = Line.of("신분당선", "red");

	@DisplayName("구간제거/상행종착역")
	@Test
	void delete_upTerminal() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 양재역, 4));
		sections.add(Section.of(신분당선, 양재역, 판교역, 6));
		sections.add(Section.of(신분당선, 판교역, 광교역, 3));

		sections.delete(강남역);

		final Section upTerminalSection = sections.getUpTerminalSection();
		assertThat(upTerminalSection.getUpStation()).isEqualTo(양재역);
		assertThat(upTerminalSection.getDownStation()).isEqualTo(판교역);
		assertThat(upTerminalSection.getDistance()).isEqualTo(6);

		assertThat(sections.getAllStationSortedByUpToDownFrom(upTerminalSection))
			.containsExactly(양재역, 판교역, 광교역);
	}

	@DisplayName("구간제거/하행종착역")
	@Test
	void delete_downTerminal() {
		final Sections sections = new Sections();
		final Section firstSection = Section.of(신분당선, 강남역, 양재역, 4);
		sections.add(firstSection);
		sections.add(Section.of(신분당선, 양재역, 판교역, 6));
		sections.add(Section.of(신분당선, 판교역, 광교역, 3));

		sections.delete(광교역);

		assertThat(sections.getAllStationSortedByUpToDownFrom(firstSection))
			.containsExactly(강남역, 양재역, 판교역);
	}

	@DisplayName("구간제거/구간사이역")
	@Test
	void delete_between() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 양재역, 4));
		sections.add(Section.of(신분당선, 양재역, 판교역, 6));
		sections.add(Section.of(신분당선, 판교역, 광교역, 3));

		sections.delete(양재역);

		final Section upTerminalSection = sections.getUpTerminalSection();
		assertThat(upTerminalSection.getUpStation()).isEqualTo(강남역);
		assertThat(upTerminalSection.getDownStation()).isEqualTo(판교역);
		assertThat(upTerminalSection.getDistance()).isEqualTo(10);

		assertThat(sections.getAllStationSortedByUpToDownFrom(upTerminalSection))
			.containsExactly(강남역, 판교역, 광교역);
	}

	@DisplayName("구간제거/유일한 구간의 역 제거시 실패")
	@Test
	void delete_the_only_1_remaining() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 양재역, 4));

		assertThatExceptionOfType(DeletableSectionNotFoundException.class)
			.isThrownBy(() -> sections.delete(강남역))
			.withMessage(DeletableSectionNotFoundException.MESSAGE);
	}

	@DisplayName("구간제거/노선에 존재하지 않는 역 제거시 실패")
	@Test
	void delete_not_found_station_in_line() {
		final Sections sections = new Sections();
		sections.add(Section.of(신분당선, 강남역, 양재역, 4));
		sections.add(Section.of(신분당선, 양재역, 판교역, 6));
		final Station 신분당선아닌역1 = Station.of("서울역");

		assertThatExceptionOfType(SectionNotFoundException.class)
			.isThrownBy(() -> sections.delete(신분당선아닌역1))
			.withMessage(SectionNotFoundException.MESSAGE);
	}
}
