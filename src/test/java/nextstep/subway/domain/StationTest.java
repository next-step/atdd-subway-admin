package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTest {

    @Test
    @DisplayName("Station get Name Test")
    void getStationName() {
        Station station = new Station("새로운역");
        assertThat(station.getName()).isEqualTo("새로운역");
    }
}
