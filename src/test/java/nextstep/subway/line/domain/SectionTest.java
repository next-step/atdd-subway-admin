package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.common.exception.IllegalStationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    void test_지하철_역이_null_이면_예외_발생() {
        assertAll(
            () -> assertThatThrownBy(() -> Section.of(null, new Station("서울역"), 10))
                .isInstanceOf(IllegalStationException.class),
            () -> assertThatThrownBy(() -> Section.of(new Station("서울역"), null, 10))
                .isInstanceOf(IllegalStationException.class)
        );
    }
}