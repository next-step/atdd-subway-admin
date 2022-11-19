package nextstep.subway.line;

import io.restassured.RestAssured;
import nextstep.subway.dto.request.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선도 생성 테스트")
    public void crete_line_test() {
        // When
        LineRequest lineRequest = new LineRequest("신분당선", "red"
                                                    , LocalTime.of(05, 38).format(DateTimeFormatter.ISO_TIME)
                                                    , LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME), "5");
        reqeust_register_line(lineRequest);

        // Then
        List<String> lineNames = reqeust_get_line_names();
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    private List<String> reqeust_get_line_names() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name");
    }

    private void reqeust_register_line(LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());
        params.put("startTime", lineRequest.getStartTime());
        params.put("endTime", lineRequest.getEndTime());
        params.put("intervalTime", lineRequest.getIntervalTime());

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }
}
