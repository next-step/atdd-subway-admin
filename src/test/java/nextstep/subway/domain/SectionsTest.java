package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    @DisplayName("빈 구간들로부터 구간들의 상행 지하철역을 구할때 예외 테스트")
    @Test
    void upStationFromEmptySections() {
        Sections sections = new Sections();
        assertThatIllegalStateException()
                .isThrownBy(sections::upStation)
                .withMessage("지하철 구간이 비어있습니다.");
    }

    @DisplayName("빈 구간들로부터 구간들의 하행 지하철역을 구할때 예외 테스트")
    @Test
    void downStationFromEmptySections() {
        Sections sections = new Sections();
        assertThatIllegalStateException()
                .isThrownBy(sections::downStation)
                .withMessage("지하철 구간이 비어있습니다.");
    }
}
