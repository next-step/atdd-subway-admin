package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.StationTest;
import nextstep.subway.station.domain.Station;

@DisplayName("구간 도메인 테스트")
public class SectionTest {

	public static final Section SECTION_1 = Section.of(1L, StationTest.노포역, StationTest.서면역, 10);
	public static final Section SECTION_2 = Section.of(2L, StationTest.범내골역, StationTest.다대포해수욕장역, 20);

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

}
