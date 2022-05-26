package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationsTest {
    private Stations stations;

    @BeforeEach
    void setup() {
        stations = new Stations();
    }

    @Test
    @DisplayName("동일한 아이디의 지하철역 삽입")
    void addStations_sameId() {
        stations.addStation(new Station(1L, "서초역"));
        stations.addStation(new Station(1L, "서초역_"));
        assertThat(stations.getLists()).hasSize(1);
    }

    @Test
    @DisplayName("다른 아이디의 지하철역 삽입")
    void addStations_anotherId() {
        stations.addStation(new Station(1L, "서초역"));
        stations.addStation(new Station(2L, "교대역"));
        assertThat(stations.getLists()).hasSize(2);
    }
}
