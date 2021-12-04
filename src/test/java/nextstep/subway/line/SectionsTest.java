package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
      .containsSequence(stationOf(3L, "서초역"), stationOf(2L, "교대역"), stationOf(4L, "강남역"), stationOf(1L, "선릉역"));
  }

  private Sections 지하철_2호선_구간들_생성() {
    Line secondLine = lineOf(1L, "2호선", "green");

    Station gangnamStation = stationOf(4L, "강남역");
    Station seonLeungStation = stationOf(1L, "선릉역");
    Station kyodaeStation = stationOf(2L, "교대역");
    Station seochoStation = stationOf(3L, "서초역");

    Section firstSection = new Section(1L, secondLine, gangnamStation, seonLeungStation, Distance.of(10));
    Section secondSection = new Section(2L, secondLine, kyodaeStation, gangnamStation, Distance.of(10));
    Section thirdSection = new Section(2L, secondLine, seochoStation, kyodaeStation, Distance.of(10));

    return new Sections(new ArrayList<>(Arrays.asList(firstSection, secondSection, thirdSection)));
  }

  @DisplayName("구간 생성 요청 상행 역과 기존에 상행역이 일치할 경우")
  @Test
  void 역과_역_사이_구간_추가_상행역_일치한_경우() {
    // given
    Sections secondSections = 지하철_2호선_구간들_생성();
    Section newSection = Section.of(stationOf(4L, "강남역"), stationOf(5L, "역삼역"), Distance.of(4));

    // when
    secondSections.add(newSection);

    // then
    assertThat(secondSections.getUpToDownStations().getStations())
      .containsExactly(
        stationOf(3L, "서초역"),
        stationOf(2L, "교대역"),
        stationOf(4L, "강남역"),
        stationOf(5L, "역삼역"),
        stationOf(1L, "선릉역"));

    assertThat(secondSections.getSections().stream()
      .filter(section -> section.getDownStation().getName().equals("선릉역"))
      .findFirst()
      .get().getDistance())
      .isEqualTo(Distance.of(6));
  }

  @DisplayName("구간 생성 요청 상행 역과 기존에 하행역이 일치할 경우")
  @Test
  void 역과_역_사이_구간_추가_하행역_일치한_경우() {
    Sections secondSections = 지하철_2호선_구간들_생성();

    Section newSection = Section.of(stationOf(6L, "역삼역"), stationOf(1L, "선릉역"), Distance.of(2));

    secondSections.add(newSection);

    assertThat(secondSections.getUpToDownStations().getStations())
      .containsExactly(
        stationOf(3L, "서초역"),
        stationOf(2L, "교대역"),
        stationOf(4L, "강남역"),
        stationOf(6L, "역삼역"),
        stationOf(1L, "선릉역"));

    assertThat(secondSections.getSections().stream()
      .filter(section -> section.getUpStation().getName().equals("강남역")
        && section.getDownStation().getName().equals("역삼역"))
      .findFirst()
      .get().getDistance())
      .isEqualTo(Distance.of(8));
  }

  @DisplayName("상행 종점이 추가되어 기존 상행 종점 앞에 구간 추가")
  @Test
  void 상행_종점이_추가된_경우_구간_추가() {
    Sections secondSections = 지하철_2호선_구간들_생성();

    Section newSection = Section.of(stationOf(12L, "방배역"), stationOf(3L, "서초역"), Distance.of(2));

    secondSections.add(newSection);

    assertThat(secondSections.getUpToDownStations().getStations())
      .containsExactly(
        stationOf(12L, "방배역"),
        stationOf(3L, "서초역"),
        stationOf(2L, "교대역"),
        stationOf(4L, "강남역"),
        stationOf(1L, "선릉역"));
  }

  @DisplayName("하행 종점이 추가되어 기존 하행 종점 뒤에 구간 추가")
  @Test
  void 하행_종점이_추가된_경우_구간_추가() {
    Sections secondSections = 지하철_2호선_구간들_생성();

    Section newSection = Section.of(stationOf(1L, "선릉역"), stationOf(40L, "삼성역"), Distance.of(2));

    secondSections.add(newSection);

    assertThat(secondSections.getUpToDownStations().getStations())
      .containsExactly(
        stationOf(3L, "서초역"),
        stationOf(2L, "교대역"),
        stationOf(4L, "강남역"),
        stationOf(1L, "선릉역"),
        stationOf(40L, "삼성역"));
  }
}
