package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
class LineTest {
    private Station 강남역;
    private Station 종합운동장역;
    private Station 잠실역;
    private Line _2호선;

    @BeforeEach
    void beforeEach() {
        강남역 = new Station("강남역");
        종합운동장역 = new Station("종합운동장역");
        잠실역 = new Station("잠실역");
        _2호선 = new Line("2호선", "bg-green-200", 강남역, 종합운동장역, 1000L);
    }

    @DisplayName("`Line`의 이름, 색상 변경")
    @Test
    void update() {
        // Given
        String expectedName = "3호선";
        String expectedColor = "bg-red-400";
        // When
        _2호선.update(expectedName, expectedColor);
        // Then
        assertAll(
                () -> assertThat(_2호선.getName()).isEqualTo(expectedName),
                () -> assertThat(_2호선.getColor()).isEqualTo(expectedColor)
        );
    }

    @DisplayName("노선의 지하철역 `Station` 제거")
    @Test
    void deleteStation() {
        // Given
        _2호선.addSection(종합운동장역, 잠실역, 1000L);
        // When
        _2호선.deleteStation(종합운동장역);
        // Then
        assertThat(_2호선.getStations())
                .extracting(Station::getName)
                .containsExactly("강남역", "잠실역");
    }
}
