package nextstep.subway.station.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.exception.StationNotFoundException;

class StationsTest {

    @Test
    void 지하철역_존재하지_않는_경우_예외() {
        Assertions.assertThatThrownBy(() -> {
                      Stations stations = new Stations();
                      stations.getStations();
                  }).isInstanceOf(StationNotFoundException.class)
                  .hasMessage("지하철 역이 존재하지 않습니다.");
    }

}