package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("지하철 노선 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선도 생성 테스트")
    public void crete_line_test() {
        // Given
        LineRequest lineRequest = new LineRequest("신분당선", "red"
                , LocalTime.of(05, 38).format(DateTimeFormatter.ISO_TIME)
                , LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME), "5");

        // When
        reqeust_register_line(lineRequest);
        List<String> lineNames = reqeust_get_line_names();

        // Then
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @Test
    @DisplayName("지하철 노선도 생성 실패 테스트")
    public void crete_line_with_duplicate_name_test() {
        // Given
        LineRequest lineRequest = new LineRequest("신분당선", "red"
                , LocalTime.of(05, 38).format(DateTimeFormatter.ISO_TIME)
                , LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME), "5");

        // When
        reqeust_register_line(lineRequest);
        ExtractableResponse<Response> response = reqeust_register_line(lineRequest);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private List<String> reqeust_get_line_names() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name");
    }

    private ExtractableResponse<Response> reqeust_register_line(LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());
        params.put("startTime", lineRequest.getStartTime());
        params.put("endTime", lineRequest.getEndTime());
        params.put("intervalTime", lineRequest.getIntervalTime());

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선도 목록 조회 테스트")
    public void get_lines_test() {
        // Given
        LineRequest firstLineRequest = new LineRequest("신분당선", "red"
                , LocalTime.of(05, 38).format(DateTimeFormatter.ISO_TIME)
                , LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME), "5");
        LineRequest secondLineRequest = new LineRequest("6호선", "orange"
                , LocalTime.of(05, 38).format(DateTimeFormatter.ISO_TIME)
                , LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME), "5");

        // When
        reqeust_register_line(firstLineRequest);
        reqeust_register_line(secondLineRequest);
        List<String> lineNames = reqeust_get_line_names();

        // Then
        assertThat(lineNames).contains("신분당선", "6호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회 테스트")
    public void get_line_test() {
        // Given
        LineRequest firstLineRequest = new LineRequest("신분당선", "red"
                , LocalTime.of(05, 38).format(DateTimeFormatter.ISO_TIME)
                , LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME), "5");

        // When
        ExtractableResponse<Response> createLine = reqeust_register_line(firstLineRequest);
        String name = RestAssured.given().log().all()
                .pathParam("id", createLine.jsonPath().get("id"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/line/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().get("name");

        // Then
        assertThat(name).isEqualTo("신분당선");
    }
}
