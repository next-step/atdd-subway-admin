package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.AppException;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.StationTest;
import nextstep.subway.station.domain.Station;

@DisplayName("구간 도메인 테스트")
public class SectionTest {

	public static final Section SECTION_1 = Section.of(1L, StationTest.노포역, StationTest.서면역, 10);
	public static final Section SECTION_2 = Section.of(2L, StationTest.부산진역, StationTest.다대포해수욕장역, 20);
	public static final Section SECTION_3 = Section.of(3L, StationTest.서면역, StationTest.범내골역, 20);

	@Test
	@DisplayName("생성한다")
	void create() {
		// given, when
		Section section = Section.of(1L, StationTest.노포역, StationTest.다대포해수욕장역, 40);

		// then
		assertThat(section).isEqualTo(SECTION_1);
	}

	@Test
	@DisplayName("역들을 반환한다")
	void getStationsTest() {
		// given, when
		List<Station> stations = SECTION_1.getStations();
		// then
		assertThat(stations.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("역들을 상행선부터 하행선 순으로 정렬되어 반환한다")
	void getStationsTest2() {
		// given, when
		List<Station> stations = SECTION_1.getStations();
		// then
		assertAll(
			() -> assertThat(stations.get(0)).isEqualTo(SECTION_1.getUpStation()),
			() -> assertThat(stations.get(1)).isEqualTo(SECTION_1.getDownStation())
		);
	}

	@Test
	@DisplayName("앞에 다른 구간을 추가한다")
	void addSectionBeforeTest() {
		// given
		Section section = Section.of(1L, StationTest.서면역, StationTest.부산진역, 10);
		Section frontSection = Section.of(2L, StationTest.서면역, StationTest.범내골역, 2);

		// when
		section.updateUpStation(frontSection);

		// then
		assertAll(
			() -> assertThat(section.getDistance()).isEqualTo(8),
			() -> assertThat(section.getUpStation()).isEqualTo(StationTest.범내골역),
			() -> assertThat(section.getDownStation()).isEqualTo(StationTest.부산진역),
			() -> assertThat(frontSection.getDistance()).isEqualTo(2),
			() -> assertThat(frontSection.getUpStation()).isEqualTo(StationTest.서면역),
			() -> assertThat(frontSection.getDownStation()).isEqualTo(StationTest.범내골역)
		);
	}

	@Test
	@DisplayName("상행역 변경 시, 기존 길이보다 크거나 같으면 안된다")
	void addSectionBeforeTest2() {
		// given
		Section section = Section.of(1L, StationTest.서면역, StationTest.부산진역, 10);
		Section frontSection = Section.of(2L, StationTest.서면역, StationTest.범내골역, 10);

		// when
		assertThatThrownBy(() -> section.updateUpStation(frontSection))
			.isInstanceOf(AppException.class);
	}

}
