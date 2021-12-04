package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.StationTest;
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
		List<Station> stations = sections.getStations();

		// then
		assertThat(stations.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("역 목록 정렬 테스트")
	void getStationsSortTest() {
		// given
		Sections sections = Sections.of(Arrays.asList(SectionTest.SECTION_2, SectionTest.SECTION_1));

		// when
		List<Station> stations = sections.getStations();

		// then
		assertThat(stations).containsExactly(
			StationTest.노포역, StationTest.서면역, StationTest.범내골역, StationTest.다대포해수욕장역);
	}

}
