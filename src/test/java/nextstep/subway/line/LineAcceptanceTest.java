package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
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

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    static final String URL_PATH_LINES = "/lines";
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_COLOR = "color";

    @LocalServerPort
    int port;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    LineRepository lineRepository;
    Station upStation1, upStation2, downStation1, downStation2;
    LineRequest lineCreateRequest1, lineCreateRequest2;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        upStation1 = stationRepository.save(new Station(1L, "광교역"));
        upStation2 = stationRepository.save(new Station(2L, "신사역"));
        downStation1 = stationRepository.save(new Station(3L, "석남역"));
        downStation2 = stationRepository.save(new Station(4L, "장암역"));
        lineCreateRequest1 = new LineRequest(
                "신분당선",
                "bg-red-600",
                upStation1.getId().toString(),
                downStation1.getId().toString(),
                "10");
        lineCreateRequest2 = new LineRequest(
                "7호선",
                "bg-brown-600",
                upStation2.getId().toString(),
                downStation2.getId().toString(),
                "10");
    }

    @AfterEach
    void setDown() {
        lineRepository.deleteAll();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLine(lineCreateRequest1);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(getLineNames()).containsAnyOf(lineCreateRequest1.getName());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLine(lineCreateRequest1);
        createLine(lineCreateRequest2);

        // when
        List<String> lineNames = getLineNames();

        // then
        assertThat(lineNames).containsAnyOf(lineCreateRequest1.getName(), lineCreateRequest2.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long lineId = createLine(lineCreateRequest1).jsonPath().getLong(KEY_ID);

        // when
        ExtractableResponse<Response> response = findByLineId(lineId);

        // then
        assertThat(response.jsonPath().getString(KEY_NAME)).isEqualTo(lineCreateRequest1.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long lineId = createLine(lineCreateRequest1).jsonPath().getLong(KEY_ID);

        // when
        LineRequest updateLineRequest = new LineRequest("뉴분당선", "bg-red-600");
        ExtractableResponse<Response> response = updateLine(lineId, updateLineRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        assertThat(findByLineId(lineId).jsonPath().getString(KEY_NAME)).isEqualTo(updateLineRequest.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        long lineId = createLine(lineCreateRequest1).jsonPath().getLong(KEY_ID);

        // when
        assertThat(deleteLine(lineId).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(getLineNames()).doesNotContain(lineCreateRequest1.getName());

    }

    private List<String> getLineNames() {
        return RestAssured.given().log().all()
                .when().get(URL_PATH_LINES)
                .then().log().all()
                .extract().jsonPath().getList(KEY_NAME, String.class);
    }

    private ExtractableResponse<Response> findByLineId(long lineId) {
        return RestAssured.given().log().all()
                .when().get(URL_PATH_LINES + "/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(LineRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_NAME, request.getName());
        params.put(KEY_COLOR, request.getColor());
        params.put("upStationId", request.getUpStationId());
        params.put("downStationId", request.getDownStationId());
        params.put("distance", request.getDistance());
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URL_PATH_LINES)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateLine(long lineId, LineRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_NAME, request.getName());
        params.put(KEY_COLOR, request.getColor());
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URL_PATH_LINES + "/" + lineId)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteLine(long lineId) {
        return RestAssured.given().log().all()
                .when().delete(URL_PATH_LINES + "/" + lineId)
                .then().log().all().extract();
    }

    private static class LineRequest {
        private final String name;
        private final String color;
        private final String upStationId;
        private final String downStationId;
        private final String distance;

        public LineRequest(String name, String color) {
            this.name = name;
            this.color = color;
            this.upStationId = null;
            this.downStationId = null;
            this.distance = null;
        }

        public LineRequest(String name, String color, String upStationId, String downStationId, String distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public String getUpStationId() {
            return upStationId;
        }

        public String getDownStationId() {
            return downStationId;
        }

        public String getDistance() {
            return distance;
        }
    }
}
