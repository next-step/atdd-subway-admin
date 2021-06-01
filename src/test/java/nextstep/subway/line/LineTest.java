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
    Station givenUpStation = Station.of(1L, "강남역");
    Station givenDownStation = Station.of(2L, "광교역");
    Section givenSection = new Section(givenUpStation, givenDownStation, 10);
    givenLine.addSection(givenSection);

    //when
    List<Station> actual = givenLine.getEachEndStations();

    //then
    assertThat(actual).isEqualTo(Lists.list(givenUpStation, givenDownStation));
  }
}
