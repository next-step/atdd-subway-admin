package nextstep.subway.line;

import static nextstep.subway.station.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.AppException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

@DisplayName("구간들 도메인 테스트")
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
		Section section2 = Section.of(2L, 서면역, 부산진역, 3);
		Section section3 = Section.of(3L, 부산진역, 다대포해수욕장역, 10);

		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));

		// when
		List<Station> stations = sections.getOrderedStations();

		// then
		assertThat(stations).containsExactly(노포역, 서면역, 부산진역, 다대포해수욕장역);
	}

	@Test
	@DisplayName("구간들 사이에 새 구간을 추가한다")
	void updateSectionsTest() {
		// given
		Section section1 = Section.of(1L, 노포역, 서면역, 10);
		Section section2 = Section.of(2L, 서면역, 부산진역, 5);
		Section section3 = Section.of(3L, 부산진역, 다대포해수욕장역, 10);
		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));
		Section section4 = Section.of(4L, 서면역, 범내골역, 1);

		// when
		sections.update(section4);

		// then
		assertThat(sections.getOrderedStations())
			.containsExactly(노포역, 서면역, 범내골역, 부산진역, 다대포해수욕장역);
		assertThat(section2.getDistance()).isEqualTo(Distance.of(4));
	}

	@Test
	@DisplayName("상행종점에 새 구간을 추가한다")
	void updateSectionsTest2() {
		// given
		Section section1 = Section.of(1L, 노포역, 서면역, 10);
		Section section2 = Section.of(2L, 서면역, 부산진역, 5);
		Section section3 = Section.of(3L, 부산진역, 다대포해수욕장역, 10);
		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));
		Station 새상행종점 = Station.of(99L, "새상행종점");
		Section section4 = Section.of(4L, 새상행종점, 노포역, 1);

		// when
		sections.update(section4);

		// then
		assertThat(sections.getOrderedStations())
			.containsExactly(새상행종점, 노포역, 서면역, 부산진역, 다대포해수욕장역);
	}

	@Test
	@DisplayName("하행종점에 새 구간을 추가한다")
	void updateSectionsTest3() {
		// given
		Section section1 = Section.of(1L, 노포역, 서면역, 10);
		Section section2 = Section.of(2L, 서면역, 부산진역, 5);
		Section section3 = Section.of(3L, 부산진역, 다대포해수욕장역, 10);
		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));
		Station 새하행종점 = Station.of(99L, "새하행종점");
		Section section4 = Section.of(4L, 다대포해수욕장역, 새하행종점, 1);

		// when
		sections.update(section4);

		// then
		assertThat(sections.getOrderedStations())
			.containsExactly(노포역, 서면역, 부산진역, 다대포해수욕장역, 새하행종점);
	}

	@Test
	@DisplayName("상행과 하행역이 기존역들과 같을 시, 추가 안됨")
	void updateSectionsTest4() {
		// given
		Section section1 = Section.of(1L, 노포역, 서면역, 10);
		Section section2 = Section.of(2L, 서면역, 부산진역, 5);
		Section section3 = Section.of(3L, 부산진역, 다대포해수욕장역, 10);
		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));
		Section section4 = Section.of(4L, 부산진역, 다대포해수욕장역, 1);

		// when
		assertThatThrownBy(() -> sections.update(section4))
			.isInstanceOf(AppException.class);
	}

	@Test
	@DisplayName("상행과 하행역이 기존 구간에 없을 시, 추가 안됨")
	void updateSectionsTest5() {
		// given
		Section section1 = Section.of(1L, 노포역, 서면역, 10);
		Section section2 = Section.of(2L, 서면역, 부산진역, 5);
		Section section3 = Section.of(3L, 부산진역, 다대포해수욕장역, 10);
		Sections sections = Sections.of(Arrays.asList(section1, section2, section3));
		Station 새상행종점 = Station.of(98L, "새상행종점");
		Station 새하행종점 = Station.of(99L, "새하행종점");
		Section section4 = Section.of(4L, 새상행종점, 새하행종점, 1);

		// when
		assertThatThrownBy(() -> sections.update(section4))
			.isInstanceOf(AppException.class);
	}

}
