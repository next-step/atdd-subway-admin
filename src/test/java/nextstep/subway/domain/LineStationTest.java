package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 구간역")
public class LineStationTest {


    @Test
    @DisplayName("상행 노선 필수이다.")
    void isNotNullUpstationLine() {
        assertThatIllegalArgumentException().isThrownBy(() -> Section.builder()
                .upStation(Station.createStation("주안역"))
                .distance(Distance.of(3))
                .build());

    }

    @Test
    @DisplayName("하행 노선 필수이다.")
    void isNotNullDowuStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> Section.builder()
                .upStation(Station.createStation("인천역"))
                .downStation(Station.createStation("주안역"))
                .distance(3).build());

    }

    @Test
    @DisplayName("거리는 필수이다.")
    void isNotNullLine() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Section.builder()
                        .upStation(Station.createStation("주안역"))
                        .downStation(Station.createStation("인천역"))
                        .build());

    }


}
