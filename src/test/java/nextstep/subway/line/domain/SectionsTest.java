package nextstep.subway.line.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.domain.LineTest.LINE_2호선;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private static final Sections sections = new Sections();

    @BeforeAll
    static void setUp(){
        sections.add(new Section(강남역, 역삼역, LINE_2호선, 10));
        sections.add(new Section(역삼역, 양재역, LINE_2호선, 50));
    }

    @Test
    @DisplayName("구간 목록 추가")
    void addTest(){
        Sections actual = new Sections();
        actual.add(new Section(강남역, 역삼역, LINE_2호선, 10));
        actual.add(new Section(역삼역, 양재역, LINE_2호선, 50));
        assertThat(actual).isEqualTo(sections);
    }

    @Test
    @DisplayName("첫번째 상행선 역 검색")
    void findFirstUpStationTest(){
        Station actual = sections.findFirstUpStation();
        assertThat(actual).isEqualTo(강남역);
    }

    @Test
    @DisplayName("구간이 존재하지 않으면 Exception 발생")
    void findFirstUpStationNotFound(){
        Sections actual = new Sections();
        assertThatThrownBy(actual::findFirstUpStation).isInstanceOf(NotFoundStationException.class);
    }

    @Test
    @DisplayName("구간 지하철역 목록 조회")
    void getStationsTest(){
        Stations stations = sections.getStations();
        assertThat(stations.getStations()).hasSize(3);
        assertThat(stations.getStations()).containsExactly(강남역, 역삼역, 양재역);
    }

}