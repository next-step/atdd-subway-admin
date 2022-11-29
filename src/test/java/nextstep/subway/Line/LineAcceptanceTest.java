package nextstep.subway.Line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    StationService stationService;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
        creatStation("강남역");
        creatStation("서초역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLine() {
        return Stream.of(
                dynamicTest("지하철 노선 생성 요청을 통해 새로운 지하철 노선을 생성", () -> {
                    ExtractableResponse<Response> response = createLine(generateRequest("2호선", "green"));
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }),

                dynamicTest("지하철 노선 목록 조회 요청을 통해 생성한 지하철 노선을 확인", () -> {
                    List<String> lineNames = showLines();
                    assertThat(lineNames).containsAnyOf("2호선");
                })
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회.")
    @TestFactory
    Stream<DynamicTest> showCreatedLines() {
        return Stream.of(
            dynamicTest("지하철 노선 생성 요청을 통해 새로운 지하철 노선 2 건 생성", () -> {
                LineRequest request1 = generateRequest("2호선", "green");
                LineRequest request2 = generateRequest("3호선", "orange");

                ExtractableResponse<Response> response1 = createLine(request1);
                ExtractableResponse<Response> response2 = createLine(request2);

                assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("지하철 노선 목록 조회를 통해 생성된 2개의 노선 조회", () -> {
                List<String> lineNames = showLines();
                assertThat(lineNames).hasSize(2);

            })
        );
    }
    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 특정 노선 조회.")
    @TestFactory
    Stream<DynamicTest> retrieveTheLine() {
        final long[] id = new long[1];
        return Stream.of(
                dynamicTest("지하철 노선 생성 요청을 통해 새로운 노선 생성", () -> {
                    ExtractableResponse<Response> response = createLine(generateRequest("2호선", "green"));
                    id[0] = response.body().jsonPath().getLong("id");
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }),

                dynamicTest("생선한 노선의 id를 통해 조회 요청 시 생성 노선 조회 가능", () -> {
                    ExtractableResponse<Response> retrieveResponse = retrieveLine(id[0]);
                    assertThat(retrieveResponse.body().jsonPath().getString("name")).contains("2호선");
                })
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 특정 노선 수정.")
    @TestFactory
    Stream<DynamicTest> modifyTheLine() {
        final long[] id = new long[1];
        return Stream.of(
                dynamicTest("지하철 노선을 생성하고", () -> {
                    ExtractableResponse<Response> response = createLine(generateRequest("2호선", "green"));
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    id[0] = response.body().jsonPath().getLong("id");
                }),

                dynamicTest("생성한 지하철 노선의 id를 통해 수정요청을 하여 노선 정보 수정", () -> {
                    LineRequest modifyRequest = generateRequest("3호선", "orange");
                    ExtractableResponse<Response> modifyResponse = modifyLine(modifyRequest, id[0]);
                    assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("지하철 노선 수정된 정보 확인", () -> {
                    ExtractableResponse<Response> retrieveResponse = retrieveLine(id[0]);
                    assertThat(retrieveResponse.body().jsonPath().getString("name")).contains("3호선");
                })
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 특정 노선 삭제.")
    @TestFactory
    Stream<DynamicTest> deleteTheLine() {
        final long[] id = new long[1];
        return Stream.of(
                dynamicTest("지하철 노선을 생성", () -> {
                    LineRequest request = generateRequest("2호선", "green");
                    ExtractableResponse<Response> response = createLine(request);
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    id[0] = response.body().jsonPath().getLong("id");
                }),

                dynamicTest("생성한 지하철을 삭제", () -> {
                    ExtractableResponse<Response> modifyResponse = deleteLine(id[0]);
                    assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),

                dynamicTest("노선정보 조회 후 삭제한 지하철 미조회 확인", () -> {
                    ExtractableResponse<Response> retrieveResponse = retrieveLine(id[0]);
                    assertThat(retrieveResponse.body().jsonPath().getString("name")).isNull();
                })
        );
    }

    private List<String> showLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> createLine(LineRequest params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> retrieveLine(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines" + DELIMITER + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> modifyLine(LineRequest modifyParams, long id) {
        return RestAssured.given().log().all()
                .body(modifyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines" + DELIMITER + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines" + DELIMITER + id)
                .then().log().all()
                .extract();
    }

    private LineRequest generateRequest(String name, String color) {
        return new LineRequest(name, color, 1, 2, 1);
    }
}
