package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class LineTest {

	@DisplayName("노선의 역 목록 조회")
	@Test
	void getStationsTest() {
		//given
		Line lineShinBunDang = new Line("신분당선", "red");
		Station upStationGangNam = new Station("강남역");
		Station downStationPanGyo = new Station("판교역");
		Section firstSection = new Section(lineShinBunDang, upStationGangNam, downStationPanGyo, new Distance(5));
		lineShinBunDang.addSection(firstSection);

		Station upStationPanGyo = new Station("판교역");
		Station downStationGwangGyo = new Station("광교역");
		Section secondSection = new Section(lineShinBunDang, upStationPanGyo, downStationGwangGyo, new Distance(7));
		lineShinBunDang.addSection(secondSection);

		//when
		List<Station> actual = lineShinBunDang.getStations();

		//then
		assertThat(actual).isEqualTo(Lists.list(upStationGangNam, downStationPanGyo, downStationGwangGyo));
	}
}
