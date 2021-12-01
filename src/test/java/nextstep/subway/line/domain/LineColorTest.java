package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidEntityRequiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void createLineColor() {
        // then
        assertThat(lineColor.getColor()).isEqualTo("green");
    }

    @DisplayName("노선 색깔은 빈 색깔을 가질 수 없다.")
    @Test
    void emptyLineColorException() {
        assertThatThrownBy(() -> {
            final LineColor emptyLineColor = new LineColor("");
        }).isInstanceOf(InvalidEntityRequiredException.class)
        .hasMessageContaining("필수로 필요한 Entity member가 들어오지 않았습니다.");
    }

}