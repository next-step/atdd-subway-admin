package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.TestLineFactory.lineOf;
import static nextstep.subway.line.TestLineFactory.stationOf;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
  @Test
  void 구간에_속한_지하철역_목록_조회() {
    // given
    Sections sections = 지하철_2호선_구간들_생성();

    // when
    Stations secondLineStations = sections.getUpToDownStations();

    // then
    구간에_속한_지하철역_목록_상행에서_하행_순으로_정렬됨(secondLineStations);
  }

  private void 구간에_속한_지하철역_목록_상행에서_하행_순으로_정렬됨(Stations secondLineStations) {
    assertThat(secondLineStations.getStations())
      .containsSequence(stationOf(2L, "교대역"), stationOf(5L, "강남역"), stationOf(1L, "역삼역"));
  }

  private Sections 지하철_2호선_구간들_생성() {
    Line secondLine = lineOf(1L, "2호선", "green");

    Station gangnamStation = stationOf(5L, "강남역");
    Station yeoksamStation = stationOf(1L, "역삼역");
    Station kyodaeStation = stationOf(2L, "교대역");

    Section firstSection = new Section(1L, secondLine, gangnamStation, yeoksamStation, Distance.of(10));
    Section secondSection = new Section(2L, secondLine, kyodaeStation, gangnamStation, Distance.of(10));

    return new Sections(Arrays.asList(firstSection, secondSection));
  }
}
