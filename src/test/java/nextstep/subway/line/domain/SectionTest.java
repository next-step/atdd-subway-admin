package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 테스트")
class SectionTest {

    @Test
    @DisplayName("동일한 상행역과 하행역으로 생성하면 예외가 발생한다.")
    void create() {
        // given
        Station station = new Station("2호선");

        // when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Section(station, station, new Distance(10)))
                .withMessageMatching("상행역과 하행역은 같을 수 없습니다.");
    }
}
