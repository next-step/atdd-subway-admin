package nextstep.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("노선 색깔 도메인 관련")
class LineColorTest {
    private LineColor lineColor;

    @BeforeEach
    void setUp() {
        lineColor = new LineColor("green");
    }

    @DisplayName("노선 색깔을 저장한다.")
    @Test
    void createSections() {
        // then
        assertThat(lineColor.getColor()).isEqualTo("green");
    }

}