package nextstep.subway.line.dto;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineRequestTest {

    private static final LineRequest 지하철_1호선_생성_정보 = new LineRequest("1호선", "남색", 1L, 2L, 10);

    @DisplayName("생성 정보에서 이름과 색깔을 읽어온다")
    @Test
    void toLine() {
        assertThat(지하철_1호선_생성_정보.getName()).isEqualTo("1호선");
        assertThat(지하철_1호선_생성_정보.getColor()).isEqualTo("남색");
    }

}
