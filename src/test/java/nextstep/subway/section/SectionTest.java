package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationTest;
import nextstep.subway.station.domain.Station;

@DisplayName("구간 도메인 테스트")
public class SectionTest {

	public static final Section SECTION_1 = Section.of(StationTest.노포역, StationTest.다대포해수욕장역, 40);

	@Test
	@DisplayName("생성한다")
	void create() {
		// given
		int distance = 40;

		// when
		Section section = Section.of(StationTest.노포역, StationTest.다대포해수욕장역, distance);

		// then
		assertThat(section).isEqualTo(Section.of(StationTest.노포역, StationTest.다대포해수욕장역, distance));
	}

	@Test
	@DisplayName("역들을 반환한다")
	void getStationsTest() {
		// given
		Section section = Section.of(StationTest.노포역, StationTest.다대포해수욕장역, 40);

		// when
		List<Station> stations = section.getStations();
		// then
		assertThat(stations.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("역들을 상행선부터 하행선 순으로 정렬되어 반환한다")
	void getStationsTest2() {
		// given
		Section section = Section.of(StationTest.노포역, StationTest.다대포해수욕장역, 40);

		// when
		List<Station> stations = section.getStations();
		// then
		assertAll(
			() -> assertThat(stations.get(0)).isEqualTo(section.getUpStation()),
			() -> assertThat(stations.get(1)).isEqualTo(section.getDownStation())
		);
	}

}
