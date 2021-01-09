package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.SectionRestAssuredUtils.구간_등록_요청;
import static nextstep.subway.utils.LineRestAssuredUtils.*;
import static nextstep.subway.utils.SectionRestAssuredUtils.구간_등록_요청_파라미터_생성;
import static nextstep.subway.utils.StationRestAssuredUtils.지하철_역_여러개_생성_요청;

@DisplayName("노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철_역_여러개_생성_요청();
        지하철_노선_생성_요청(노선_요청_파라미터_생성("2호선", "green"));
    }

    @Test
    @DisplayName("구간 등록할 때, 상행과 하행이 동일한 경우 400 익셉션 발생")
    void addSection() {
        // when
        Map<String, String> param = 구간_등록_요청_파라미터_생성("1", "1", "10");
        ExtractableResponse<Response> response = 구간_등록_요청(1l, param);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
