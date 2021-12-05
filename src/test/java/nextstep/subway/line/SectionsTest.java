package nextstep.subway.line;

import static nextstep.subway.line.SectionTest.*;
import static nextstep.subway.station.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

public class SectionsTest {

	@Test
	@DisplayName("생성 테스트")
	void createTest() {
		// given
		Sections expected = Sections.of(Collections.singletonList(SectionTest.SECTION_1));

		// when
		Sections sections = Sections.of(Collections.singletonList(SectionTest.SECTION_1));

		// then
		assertThat(sections).isEqualTo(expected);
	}

	@Test
	@DisplayName("추가 테스트")
	void addTest() {
		// given
		Sections sections = Sections.of();
		Sections expected = Sections.of(Collections.singletonList(SectionTest.SECTION_1));

		// when
		sections.add(SectionTest.SECTION_1);

		// then
		assertThat(sections).isEqualTo(expected);
	}

	@Test
	@DisplayName("내용 포함 테스트")
	void containsTest() {
		// given
		Sections sections = Sections.of(Collections.singletonList(SectionTest.SECTION_1));

		// when
		boolean result = sections.contains(SectionTest.SECTION_1);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("역 목록 반환 테스트")
	void getStationsTest() {
		// given
		Sections sections = Sections.of(Collections.singletonList(SectionTest.SECTION_1));

		// when
		List<Station> stations = sections.getOrderedStations();

		// then
		assertThat(stations.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("역 목록 정렬 테스트")
	void getStationsSortTest() {
		// given
		Section section1 = Section.of(1L, 노포역, 서면역, 2);
		Section section2 = Section.of(2L, 서면역, 범내골역, 3);
		Section section3 = Section.of(3L, 범내골역, 다대포해수욕장역, 10);

		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));

		// when
		List<Station> stations = sections.getOrderedStations();

		// then
		assertThat(stations).containsExactly(노포역, 서면역, 범내골역, 다대포해수욕장역);
	}

	@Test
	@DisplayName("구간을 업데이트한다")
	void updateSectionsTest() {
		// given
		Line line = Line.of(1L, "신분당선", "red");
		line.addSection(SECTION_1);
		line.addSection(SECTION_2);

		// when
		line.updateSections(SECTION_3);

		// then
		List<Station> stations = line.getOrderedStations();
		assertThat(stations).containsExactly(노포역, 범어사역, 서면역, 범내골역, 다대포해수욕장역);
	}

}
