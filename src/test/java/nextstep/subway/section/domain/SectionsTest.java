package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

  private Station 강남역 = Station.of(1L, "강남역");

  private Station 양재역 = Station.of(2L, "양재역");

  private Station 청계산입구역 = Station.of(3L, "청계산입구역");

  private Station 판교역 = Station.of(4L, "판교역");

  private Station 수지구청역 = Station.of(5L, "수지구청역");

  private Station 광교역 = Station.of(6L, "광교역");

  private Sections sections;

  @BeforeEach
  void setUp() {
    Section third = new Section(청계산입구역, 판교역, 2);
    Section first = new Section(강남역, 양재역, 2);
    Section fifth = new Section(수지구청역, 광교역, 2);
    Section second = new Section(양재역, 청계산입구역, 2);
    Section fourth = new Section(판교역, 수지구청역, 2);
    Sections prepared = new Sections();
    prepared.add(third);
    prepared.add(first);
    prepared.add(fifth);
    prepared.add(second);
    prepared.add(fourth);
    this.sections = prepared;
  }

  @DisplayName("상행 -> 하행 순서에 맞게 역을 반환한다.")
  @Test
  void sortedStationsTest() {
    assertThat(sections.getDistinctStations()).containsExactly(강남역, 양재역, 청계산입구역, 판교역, 수지구청역, 광교역);
  }

  @DisplayName("이미 등록된 역 구간을 등록할 수 없다.")
  @Test
  void addFailTest() {
    Section given1 = new Section(청계산입구역, 판교역, 8);
    Section given2 = new Section(강남역, 광교역, 8);
    assertAll(
        () -> assertThatThrownBy(() -> sections.registerNewSection(given1)).isInstanceOf(IllegalArgumentException.class),
        () -> assertThatThrownBy(() -> sections.registerNewSection(given2)).isInstanceOf(IllegalArgumentException.class)
    );
  }

  @DisplayName("역 사이에 새로운 역을 등록한다.")
  @Test
  void registerNewSectionTest() {
    //given
    Station 양재시민의숲역 = Station.of(7L, "양재시민의숲역");
    Section given = new Section(양재역, 양재시민의숲역, 1);
    //when
    sections.registerNewSection(given);
    //then
    assertThat(sections.getDistinctStations()).containsExactly(강남역, 양재역, 양재시민의숲역, 청계산입구역, 판교역, 수지구청역, 광교역);
  }

  @DisplayName("역 사이에 새로운 역을 등록할 때 기존의 역 간격보다 큰 간격을 등록할 수 없다.")
  @Test
  void registerNewSectionFailTest() {
    //given
    Station 양재시민의숲역 = Station.of(7L, "양재시민의숲역");
    Section given = new Section(양재역, 양재시민의숲역, 10);
    //when
    assertThatThrownBy(() -> sections.registerNewSection(given)).isInstanceOf(IllegalArgumentException.class);
  }

}
