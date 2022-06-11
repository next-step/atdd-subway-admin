package nextstep.subway.line.domain;

import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section(StationTest.강남역, StationTest.사당역, 10L);
    }

    @Test
    void 최종_상행라인_수정() {
        // when
        section.updateFinalUpStation(true);
        // then
        assertThat(section.isFinalUpStation()).isTrue();
    }

    @Test
    void 최종_하행라인_수정() {
        // when
        section.updateFinalDownStation(true);
        // then
        assertThat(section.isFinalDownStation()).isTrue();
    }
}
