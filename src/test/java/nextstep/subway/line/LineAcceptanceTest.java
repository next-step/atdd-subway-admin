package nextstep.subway.line;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.utils.RequestUtil.요청_성공_실패_여부_확인;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
        ExtractableResponse<Response> response = 지하철_노선_생성(
                new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10L)
        );

        // Then
        요청_성공_실패_여부_확인(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 등록된 역 정보를 이용하여 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 조회 시 생성한 노선을 찾을 수 있다
     */

    @DisplayName("등록된 역 정보를 이용하여 노선을 저장한다.")
    @Test
    void createLineTest() {
        // When
        final Station savedOldStation = 역_객체로_변환(지하철역_생성("지하철역"));
        final Station savedNewStation = 역_객체로_변환(지하철역_생성(("새로운지하철역")));
        ExtractableResponse<Response> response = 지하철_노선_생성(
                new LineRequest("신분당선", "bg-red-600", savedOldStation.getId(), savedNewStation.getId(), 10L)
        );

        // Then
        요청_성공_실패_여부_확인(response, HttpStatus.CREATED);
        // Then
        assertThat(지하철노선이름으로_검색(객체리스트로_변환(지하철_노선_검색()), "신분당선")).isTrue();

    }


    private ExtractableResponse<Response> 지하철_노선_생성(final LineRequest lineRequest) {
        return requestUtil.createLine(convertMapBy(lineRequest));
    }

    private ExtractableResponse<Response> 지하철_노선_검색() {
        return requestUtil.getLine();
    }

    private List<LineResponse> 객체리스트로_변환(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class);
    }

    private boolean 지하철노선이름으로_검색(List<LineResponse> responseList, final String lineName) {
        return responseList.stream().anyMatch(lineResponse -> Objects.equals(lineResponse.getName(), lineName));
    }

    private Map<String, String> convertMapBy(final LineRequest lineRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(lineRequest, Map.class);
    }

}
