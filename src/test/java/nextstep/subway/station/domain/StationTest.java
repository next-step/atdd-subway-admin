package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철역 entity 테스트")
class StationTest {

    @Test
    @DisplayName("두개의 지하철역 아이디가 값은지 비교")
    void isSameId() {
        Station 양재역 = new Station(1L, "양재역");
        Station 미금역 = new Station("미금역");
        assertThat(양재역.isSameId(양재역)).isTrue();
        assertThat(양재역.isSameId(미금역)).isFalse();
        assertThat(미금역.isSameId(양재역)).isFalse();
    }
}
