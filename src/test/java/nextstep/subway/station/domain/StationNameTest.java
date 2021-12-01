package nextstep.subway.station.domain;

import nextstep.subway.common.exception.InvalidEntityRequiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("역 이름 도메인 관련")
class StationNameTest {
    private StationName stationName;

    @BeforeEach
    void setUp() {
        stationName = new StationName("강남역");
    }

    @DisplayName("역 이름을 저장할 수 있다.")
    @Test
    void createStationName() {
        assertThat(stationName.getName()).isEqualTo("강남역");
    }

    @DisplayName("노선 이름은 빈 이름을 가질 수 없다.")
    @Test
    void emptyStationNameException() {
        assertThatThrownBy(() -> {
            final StationName emptyStationName = new StationName("");
        }).isInstanceOf(InvalidEntityRequiredException.class)
                .hasMessageContaining("필수로 필요한 Entity member가 들어오지 않았습니다.");
    }

}