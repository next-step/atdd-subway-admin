package nextstep.subway.line;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.utils.RequestUtil.요청_성공_실패_여부_확인;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    private static RequestUtil requestUtil = new RequestUtil();

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When  등록된 역이 없는 상태에서 노선을 저장한다.
     * Then 노선 저장이 실패 한다.
     */
    @DisplayName("등록된 역이 없으면 노선을 저장 할수 없다.")
    @Test
    void invalidCreateLineTest() {
        // When
        역_검색결과에_포함_여부_확인(역_객체_리스트로_변환(지하철역_검색()), Collections.emptyList());
        // When
        ExtractableResponse<Response> response = 지하철_라인_생성(
                new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10L)
        );

        // Then
        요청_성공_실패_여부_확인(response, HttpStatus.BAD_REQUEST);
    }


    public static ExtractableResponse<Response> 지하철_라인_생성(final LineRequest lineRequest) {
        return  requestUtil.createLine(convertMapBy(lineRequest));
    }
    private static Map<String, String> convertMapBy(final LineRequest lineRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(lineRequest, Map.class);
    }

}
