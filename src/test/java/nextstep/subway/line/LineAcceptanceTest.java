package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    StationRepository stations;

    private static final String BASE_URL = "/lines";

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
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성_테스트() {
        // when
        Station station1 = stations.save(new Station("신사역"));
        Station station2 = stations.save(new Station("광교중앙"));

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response = createLine(lineRequest);

        //then
        List<String> lines = retrieveLineNames();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lines).contains(lineRequest.getName())
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회_테스트() {
        // given
        Station station1 = stations.save(new Station("신사역"));
        Station station2 = stations.save(new Station("광교중앙"));
        Station station3 = stations.save(new Station("정자"));

        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response1 = createLine(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("분당선", "bg-red-600", station1.getId(), station3.getId(), 10);
        ExtractableResponse<Response> response2 = createLine(lineRequest2);

        // when
        List<String> lines = retrieveLineNames();

        // then
        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines).contains("신분당선", "분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회_테스트() {
        // given
        Station station1 = stations.save(new Station("신사역"));
        Station station2 = stations.save(new Station("광교중앙"));

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response = createLine(lineRequest);
        String lineId = parseIdByLocation(response.header("Location"));

        // when
        LineResponse lineResponse = retrieveLineById(lineId);

        // then
        assertAll(
                () -> assertThat(lineResponse).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName())
        );

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선_수정_테스트() {

        // given
        Station station1 = stations.save(new Station("신사역"));
        Station station2 = stations.save(new Station("광교중앙"));
        Station station3 = stations.save(new Station("정자"));

        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response1 = createLine(lineRequest1);
        String lineId = parseIdByLocation(response1.header("Location"));

        // when
        lineRequest1.setName("수인분당선");
        lineRequest1.setDownStationId(station3.getId());

        ExtractableResponse<Response> response2 = updateLine(lineId, lineRequest1);

        // then
        LineResponse lineResponse = retrieveLineById(lineId);


        assertAll(
                () -> assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("수인분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제_테스트() {

        // given
        Station station1 = stations.save(new Station("신사역"));
        Station station2 = stations.save(new Station("광교중앙"));

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response1 = createLine(lineRequest);

        String lineId = parseIdByLocation(response1.header("Location"));

        // when
        ExtractableResponse<Response> response2 = deleteLineById(lineId);

        // then
        LineResponse lineResponse = retrieveLineById(lineId);

        assertAll(
                () -> assertThat(response2.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(lineResponse).isNull()
        );
    }

    public static ExtractableResponse<Response> createLine(LineRequest lineRequest) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post(BASE_URL)
                        .then().log().all()
                        .extract();

        return response;
    }

    public static List<String> retrieveLineNames() {
        List<String> lineNames =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get(BASE_URL)
                        .then().log().all()
                        .extract()
                        .jsonPath().getList("name", String.class);

        return lineNames;
    }

    public static LineResponse retrieveLineById(String id) {

        ExtractableResponse response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(BASE_URL + "/" + id)
                .then().log().all()
                .extract();

        if (HttpStatus.BAD_REQUEST.value() == response.statusCode()) {
            return null;
        }

        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private static ExtractableResponse<Response> updateLine(String id, LineRequest lineRequest) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put(BASE_URL + "/" + id)

                        .then().log().all()
                        .extract();

        return response;
    }

    private static ExtractableResponse<Response> deleteLineById(String id) {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(BASE_URL + "/" + id)
                .then().log().all()
                .extract();

        return response;
    }

    public String parseIdByLocation(String location) {
        return location.substring(location.lastIndexOf("/")+1);
    }

    public Map<String, String> setParams(LineRequest lineRequest) {

        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());
        params.put("upStationId", String.valueOf(lineRequest.getUpStationId()));
        params.put("downStationId", String.valueOf(lineRequest.getDownStationId()));
        params.put("distance", String.valueOf(lineRequest.getDistance()));

        return params;
    }
}
