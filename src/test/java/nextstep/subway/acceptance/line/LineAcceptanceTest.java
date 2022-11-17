package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.station.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 2개의 지하철 역이 생성되어 있다.
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createStation() {

        Long 신논현역_ID = 지하철역_생성(SHINNONHYUN_STATION).jsonPath().getLong("id");
        Long 논현역_ID = 지하철역_생성(NONHYUN_STATION).jsonPath().getLong("id");
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red", 신논현역_ID, 논현역_ID, 4);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
