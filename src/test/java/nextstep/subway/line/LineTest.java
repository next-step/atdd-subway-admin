package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

  @DisplayName("노선의 상행, 하행 종점을 반환한다.")
  @Test
  void getEachEndStationsTest() {
    //given
    Line givenLine = new Line("신분당선", "red");
    Station upStationGangNam = Station.of(1L, "강남역");
    Station downStationPanGyo = Station.of(2L, "판교역");
    Section firstSection = new Section(upStationGangNam, downStationPanGyo, 5);
    givenLine.addSection(firstSection);

    Station upStationPanGyo = Station.of(2L, "판교역");
    Station downStationGwangGyo = Station.of(3L, "광교역");
    Section secondSection = new Section(upStationPanGyo, downStationGwangGyo, 7);
    givenLine.addSection(secondSection);

    //when
    List<Station> actual = givenLine.getEndToEndStations();

    //then
    assertThat(actual).isEqualTo(Lists.list(upStationGangNam, downStationPanGyo, downStationGwangGyo));
  }
}
