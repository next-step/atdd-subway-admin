package nextstep.subway.station.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("역 도메인 관련 기능")
class StationTest {
    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station("강남역");
    }

    @DisplayName("역을 생성한다.")
    @Test
    void createStation() {
        // then
        assertAll(
                () -> assertThat(station.getName()).isEqualTo("강남역")
        );
    }
}