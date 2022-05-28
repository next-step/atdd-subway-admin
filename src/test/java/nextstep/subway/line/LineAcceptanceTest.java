package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("노선역 기능(인수테스트)")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    Long downStationId;
    Long upStationId;


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        downStationId = 지하철역을_생성_한다("주안역").jsonPath().getLong("id");
        upStationId = 지하철역을_생성_한다("인천역").jsonPath().getLong("id");

    }

    /**
        When 지하철 노선을 생성하면
        Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("노선역을 생성한다.")
    void createLine() {
        //When 지하철 노선을 생성하면
        final ExtractableResponse<Response> createLine = 노선을_생성한다(new LineRequest("1호선", "bg-red-500", 10,
                downStationId, upStationId));

        //Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
        final ExtractableResponse<Response> findAllLine = 전체_노선을_찾는다();
        assertThat(createLine.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(findAllLine.jsonPath().getList("name")).contains("1호선");
    }

    /**
         Given 2개의 지하철 노선을 생성하고
         When 지하철 노선 목록을 조회하면
         Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록 조회")
    void searchLines() {
        //Given 2개의 지하철 노선을 생성하고
        노선을_생성한다(new LineRequest("1호선", "bg-red-500", 10,
                downStationId, upStationId));

        노선을_생성한다(new LineRequest("2호선", "bg-red-500", 4,
                downStationId, upStationId));

        // When 지하철 노선 목록을 조회하면
        final ExtractableResponse<Response> findLinse = 전체_노선을_찾는다();

        //Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
        assertAll(
                () -> assertThat(findLinse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findLinse.jsonPath().getList("id")).hasSize(2),
                () -> assertThat(findLinse.jsonPath().getList("name")).contains("1호선", "2호선")
        );
    }

    /**
     Given 지하철 노선을 생성하고
     When 생성한 지하철 노선을 조회하면
     Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선 조회")
    void searchLine() {
        //Given 지하철 노선을 생성하고
        final ExtractableResponse<Response> 생성된_노선 = 노선을_생성한다(new LineRequest("1호선", "bg-red-500", 10,
                downStationId, upStationId));

        final long createLineId = 생성된_노선.jsonPath().getLong("id");
        //When 생성한 지하철 노선을 조회하면
        final ExtractableResponse<Response> 조회된_노선 = 노선을_조회한다(createLineId);

        //Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
        assertAll(
            () -> assertThat(조회된_노선.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(조회된_노선.jsonPath().getString("name")).isEqualTo("1호선")
        );
    }

    /**
     Given 지하철 노선을 생성하고
     When 생성한 지하철 노선을 수정하면
     Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철노선 수정")
    void fixLine() {
        //Given 지하철 노선을 생성하고
        final ExtractableResponse<Response> 노선을_생성한다 = 노선을_생성한다(new LineRequest("1호선", "bg-red-500",
                10, downStationId, upStationId));

        //When 생성한 지하철 노선을 수정하면
        Long lineId = 노선을_생성한다.jsonPath().getLong("id");
        LineRequest lineRequest = new LineRequest();
        lineRequest.setColor("bg-blue-500");
        lineRequest.setName("2호선");

        final ExtractableResponse<Response> 노선을_수정한다 = RestAssured.given().log().all()
                .pathParam("id", lineId)
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();

        //Then 해당 지하철 노선 정보는 수정된다
        final ExtractableResponse<Response> 수정된_노선 = 노선을_조회한다(lineId);
        assertAll(
                () -> assertThat(노선을_수정한다.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(수정된_노선.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(수정된_노선.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(수정된_노선.jsonPath().getString("color")).isEqualTo("bg-blue-500")
        );

    }

    /**
     Given 지하철 노선을 생성하고
     When 생성한 지하철 노선을 삭제하면
     Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철노선 삭제")
    void deleteLine() {

    }

    private ExtractableResponse<Response> 지하철역을_생성_한다(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선을_생성한다(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/line")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 전체_노선을_찾는다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선을_조회한다(long createLineId) {
        return RestAssured.given().log().all()
                .pathParam("id", createLineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }
}
