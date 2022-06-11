package nextstep.subway.line.domain;

import nextstep.subway.station.domain.StationTest;
import nextstep.subway.station.exception.StationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section(StationTest.강남역, StationTest.사당역, 10L);
    }

    @Test
    void 지하철역_미존재() {
        // given
        // when
        // then
        assertThatThrownBy(() -> new Section(null, StationTest.사당역, 10L))
                .isInstanceOf(StationException.class);
    }
}
