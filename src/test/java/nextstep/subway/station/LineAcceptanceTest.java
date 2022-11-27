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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        upStationId = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class).getId();
        downStationId = StationAcceptanceTest.지하철역_생성("공릉역").as(StationResponse.class).getId();
    }

    /*
     *   When 지하철 노선을 생성하면
     *   Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    public void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /*
     *   Given 2개의 지하철 노선을 생성하고
     *   When 지하철 노선 목록을 조회하면
     *   Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    public void getLines() {
        // given
        LineRequest firstLineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        LineRequest secondLineRequest = new LineRequest("분당선", "green", upStationId, downStationId, 10);

        지하철_노선_생성(firstLineRequest);
        지하철_노선_생성(secondLineRequest);
        // when
        List<String> lines = 지하철_노선_조회();
        // then
        assertAll(
                () -> assertThat(lines.size()).isEqualTo(2),
                () -> assertThat(lines).containsAnyOf("신분당선", "분당선")
        );
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

    public List<String> 지하철_노선_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

}
