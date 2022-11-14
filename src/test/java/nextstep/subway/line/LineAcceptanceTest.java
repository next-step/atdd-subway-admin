package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceFixture.*;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성후_ID_를_리턴한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성한다")
    void createLine() {
        // when
        long upStationId = 지하철역_생성후_ID_를_리턴한다("강남역");
        long downStationId = 지하철역_생성후_ID_를_리턴한다("역삼역");
        ExtractableResponse<Response> response = 노선을_생성한다("1호선", "RED", upStationId, downStationId, 10);

        // then
        List<String> allLines = 모든_노선을_조회한다("name");

        상태코드를_체크한다(response.statusCode(), HttpStatus.CREATED.value());
        노선의_이름이_조회된다(allLines, "1호선");
    }
}
