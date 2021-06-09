package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SortedSectionUtilTest {

	private Station 강남역;
	private Station 구디역;
	private Station 사당역;
	private Station 판교역;

	@BeforeEach
	void setUp() {
		강남역 = new Station("강남역");
		구디역 = new Station("구디역");
		사당역 = new Station("사당역");
		판교역 = new Station("판교역");
	}

	@Test
	@DisplayName("등록된 지하철 구간 정렬 테스트")
	void sortedTest() {
		// given
		List<Section> sections = new ArrayList<>();
		sections.add(Section.of(강남역, 구디역, 10));
		sections.add(Section.of(판교역, 사당역, 3));
		sections.add(Section.of(구디역, 판교역, 5));
		SortedSectionUtil sortedSectionUtil = SortedSectionUtil.of(sections);

		// when
		List<Station> sortedStations = sortedSectionUtil.sorted();

		// then
		assertThat(sortedStations)
			.isNotEmpty()
			.containsExactly(강남역, 구디역, 판교역, 사당역);

	}

}
