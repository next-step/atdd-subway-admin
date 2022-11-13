package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.AcceptanceTestFixture.응답코드가_일치한다;
import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.station.StationAcceptanceTestFixture.지하철역_생성;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long upStationId = 지하철역_생성("강남역").jsonPath().getLong("id");
        Long downStationId = 지하철역_생성("판교역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // then
        응답코드가_일치한다(지하철_노선_생성_응답.statusCode(), HttpStatus.CREATED);
        지하철_노선_목록에_생성한_노선이_포함되어_있다(지하철_노선_이름_전체_목록(), "신분당선");
    }
}
