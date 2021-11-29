package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DisplayName("구간목록 도메인 단위테스트")
public class SectionsTest {
	@Test
	@DisplayName("구간 사이의 역이 상행역부터 하행역순으로 정렬되어 반환된다")
	void getStationsUpToDown() {
		Sections sections = new Sections();
		Line line = new Line("신분당선", "bg-red-600", sections);
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");
		sections.add(new Section(line, station1, station2, 10));
		sections.add(new Section(line, station2, station3, 10));
		List<Station> result = sections.getStationsUpToDown();

		assertThat(result).containsExactly(station1, station2, station3);
	}


	@Test
	@DisplayName("구간의 순서를 섞어서 넣어도 구간 사이의 역이 상행역부터 하행역순으로 정렬되어 반환된다")
	void getStationsUpToDown2() {
		//TODO 구간의 순서를 섞어서 넣어도 정상출력 되는지에 대한 테스트. 스탭3에서 작성할 예정
	}
}
