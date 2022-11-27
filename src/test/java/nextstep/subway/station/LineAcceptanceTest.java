package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /*
     *   When 지하철 노선을 생성하면
     *   Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    public void createLine() {
        // given
        Long upStationId = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class).getId();
        Long downStationId = StationAcceptanceTest.지하철역_생성("공릉역").as(StationResponse.class).getId();
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/liens")
                        .then().log().all()
                        .extract();

        return response;
    }


}
