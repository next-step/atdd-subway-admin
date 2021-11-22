package nextstep.subway.line.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.NotIncludeOneStationException;
import nextstep.subway.exception.SameSectionStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.domain.LineTest.LINE_2호선;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private Sections sections = new Sections();

    @BeforeEach
    void setUp() {
        sections.add(new Section(강남역, 역삼역, LINE_2호선, 10));
        sections.add(new Section(역삼역, 양재역, LINE_2호선, 50));
    }

    @Test
    @DisplayName("구간 목록 추가")
    void addTest() {
        Sections actual = new Sections();
        actual.add(new Section(강남역, 역삼역, LINE_2호선, 10));
        actual.add(new Section(역삼역, 양재역, LINE_2호선, 50));
        assertThat(actual).isEqualTo(sections);
    }

    @Test
    @DisplayName("첫번째 상행역 검색")
    void findFirstUpStationTest() {
        Station actual = sections.findFirstUpStation();
        assertThat(actual).isEqualTo(강남역);
    }

    @Test
    @DisplayName("구간이 존재하지 않으면 Exception 발생")
    void findFirstUpStationNotFound() {
        Sections actual = new Sections();
        assertThatThrownBy(actual::findFirstUpStation).isInstanceOf(NotFoundStationException.class);
    }

    @Test
    @DisplayName("구간 지하철역 목록 조회")
    void getStationsTest() {
        Stations stations = sections.getStations();
        assertThat(stations.getStations()).hasSize(3);
        assertThat(stations.getStations()).containsExactly(강남역, 역삼역, 양재역);
    }

    @Test
    @DisplayName("상행역과 하행역 모두가 구간에 등록되어 있으면 추가할 수 없다.")
    void validateSections_SameStation() {
        Section actual = new Section(강남역, 양재역, LINE_2호선, 10);
        assertThatThrownBy(() -> sections.add(actual)).isInstanceOf(SameSectionStationException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나가 구간에 포함되어있지 않으면 추가 할 수 없다.")
    void validateSections_IncludeOneStation() {

        Section actual = new Section(사당역, 잠실역, LINE_2호선, 10);
        assertThatThrownBy(() -> sections.add(actual)).isInstanceOf(NotIncludeOneStationException.class);
    }

    /*
    * - [X] 하행역이 두 역 사이에 등록 될 경우
      - [X] 상행역이 두 역 사이에 등록 될 경우
      - [X] 상행 종점이 새로 등록될 경우
      - [X] 하행 종점이 새로 등록될 경우
    * */
    @Test
    @DisplayName("하행역이 두 역 사이에 등록 될 경우")
    void betweenDownStation() {
        Section actual = new Section(강남역, 잠실역, LINE_2호선, 1);
        sections.add(actual);

        구간_검증(Arrays.asList(강남역, 잠실역, 역삼역, 양재역));
    }

    @Test
    @DisplayName("상행역이 두 역 사이에 등록 될 경우")
    void betweenUpStation() {
        Section actual = new Section(잠실역, 역삼역, LINE_2호선, 1);
        sections.add(actual);

        구간_검증(Arrays.asList(강남역, 잠실역, 역삼역, 양재역));
    }

    @Test
    @DisplayName("하행 종점이 새로 등록될 경우")
    void lastDownStation() {
        Section actual = new Section(양재역, 잠실역, LINE_2호선, 1);
        sections.add(actual);

        구간_검증(Arrays.asList(강남역, 역삼역, 양재역, 잠실역));
    }

    @Test
    @DisplayName("상행 종점이 새로 등록될 경우")
    void firstUpStation() {
        Section actual = new Section(사당역, 강남역, LINE_2호선, 1);
        sections.add(actual);

        구간_검증(Arrays.asList(사당역, 강남역, 역삼역, 양재역));
    }

    private void 구간_검증(List<Station> expected){
        assertAll(
                () -> assertThat(sections.getSections()).hasSize(3),
                () -> assertThat(sections.getStations().getStations()).containsExactlyElementsOf(expected)
        );
    }
}