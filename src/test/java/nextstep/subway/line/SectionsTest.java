package nextstep.subway.line;

import nextstep.subway.common.ServiceException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static nextstep.subway.line.TestLineFactory.lineOf;
import static nextstep.subway.line.TestLineFactory.stationOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    Section thirdSection = new Section(3L, secondLine, seochoStation, kyodaeStation, Distance.of(10));

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

  @DisplayName("종점 지하철 역이 제거될 경우")
  @Test
  void 종점_지하철_역이_제거될_경우() {
    // given
    Sections secondSections = 지하철_2호선_구간들_생성();

    // when
    secondSections.deleteByStation(stationOf(3L, "서초역"));

    // then
    assertThat(secondSections.getUpToDownStations().getStations())
        .containsExactly(
            stationOf(2L, "교대역"),
            stationOf(4L, "강남역"),
            stationOf(1L, "선릉역"));
  }

  @DisplayName("중간 지하철 역이 제거될 경우")
  @Test
  void 중간_지하철_역이_제거될_경우() {
    // given
    Sections secondSections = 지하철_2호선_구간들_생성();

    // when
    secondSections.deleteByStation(stationOf(2L, "교대역"));

    // then
    assertThat(secondSections.getUpToDownStations().getStations())
        .containsExactly(
            stationOf(3L, "서초역"),
            stationOf(4L, "강남역"),
            stationOf(1L, "선릉역"));

    assertThat(secondSections.getSections().stream()
        .filter(section -> section.getUpStation().equals(stationOf(3L, "서초역")))
        .findFirst()
        .get()
        .getDistance()).isEqualTo(Distance.of(20));
  }

  @DisplayName("노선 구간에 등록되지 않은 지하철 역 제거할 경우")
  @Test
  void 등록되지_않은_지하철_역_제거할_경우() {
    // given
    Sections secondSections = 지하철_2호선_구간들_생성();

    // when
    Throwable thrown = ThrowableAssert
        .catchThrowable(() -> secondSections.deleteByStation(stationOf(9L, "존재하지 않는 역")));

    assertThat(thrown)
        .isInstanceOf(ServiceException.class)
        .hasMessage("노선에 등록되지 않은 역은 제거할 수 없습니다.");
  }

  @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때")
  @Test
  void 구간이_하나인_노선에서_마지막_구간을_제거할_때() {
    // given
    Sections secondSections = 지하철_2호선_구간들_생성();
    secondSections.deleteByStation(stationOf(3L, "서초역"));
    secondSections.deleteByStation(stationOf(4L, "강남역"));

    // when
    Throwable thrown = ThrowableAssert
        .catchThrowable(() -> secondSections.deleteByStation(stationOf(2L, "교대역")));

    assertThat(thrown)
        .isInstanceOf(ServiceException.class)
        .hasMessage("구간이 하나 이하의 노선에서 마지막 구간을 제거할 수 없습니다.");
  }
}
