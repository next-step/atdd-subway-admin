package nextstep.subway.station.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.assertThat;

class StationsTest {

    private static Stations stations;

    @BeforeAll
    static void setUp(){
        List<Station> stationList = new ArrayList<>();
        stationList.add(강남역);
        stationList.add(역삼역);
        stations = new Stations(stationList);
    }

    @Test
    @DisplayName("지하철 역 목록 생성")
    void create(){
        List<Station> stationList = new ArrayList<>();
        stationList.add(강남역);
        stationList.add(역삼역);
        Stations stations = new Stations(stationList);
        assertThat(stations).isEqualTo(stations);
    }

    @Test
    @DisplayName("지하철 역 목록에 입력받은 역이 포함되어 있다.")
    void isInTrue(){
        boolean actual = stations.isIn(강남역);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("지하철 역 목록에서 입력받은 역이 포함되어 있지 않는다.")
    void isInFalse(){
        boolean actual = stations.isIn(양재역);

        assertThat(actual).isFalse();
    }

}