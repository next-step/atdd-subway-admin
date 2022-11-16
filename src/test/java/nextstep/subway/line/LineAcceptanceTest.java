package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String LINE_MAIN_PATH = "/lines";
    private static final String LINE_NAME = "name";
    private static final String COLOR = "color";
    private static final String DISTANCE = "distance";
    private static final String UP_STATION = "upStationId";
    private static final String DOWN_STATION = "downStationId";
    private static final String LINE_ID = "id";

    @Autowired
    private StationRepository stationRepository;
    private Station upStation;
    private Station downStation;
    private Station otherUpStation;
    private Station otherDownStation;

    @BeforeEach
    void init() {
        setUp();
        upStation = stationRepository.save(new Station("강남역"));
        downStation = stationRepository.save(new Station("성수역"));
        otherUpStation = stationRepository.save(new Station("홍대역"));
        otherDownStation = stationRepository.save(new Station("구의역"));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        ExtractableResponse<Response> saveResponse = createLine("분당선", "red", upStation.getId(), downStation.getId(), 10);
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //then
        ExtractableResponse<Response> findResponse = findAllLine();
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath responseBody = findResponse.jsonPath();
        assertThat(findListResponseByKey(responseBody, LINE_NAME)).containsExactly("분당선");
        assertThat(findListResponseByKey(responseBody, COLOR)).containsExactly("red");

    }

    /**
     * When 없는 station 으로 지하철 노선을 생성하면
     * Then 지하철 노선을 생성할 수 없다.
     */
    @DisplayName("없는 지하철로 지하철 생성 시 생성 실패")
    @Test
    void createLineFailed() {
        //when
        ExtractableResponse<Response> saveResponse = createLine("분당선", "red", 100L, 200L, 10);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findAllLineTest() {
        //give
        ExtractableResponse<Response> saveResponse = createLine("분당선", "red", upStation.getId(), downStation.getId(), 10);
        ExtractableResponse<Response> saveOtherResponse = createLine("타르코프", "yellow", otherUpStation.getId(), otherDownStation.getId(), 10);
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(saveOtherResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //when
        ExtractableResponse<Response> findResponse = findAllLine();
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        JsonPath responseBody = findResponse.jsonPath();
        assertThat(findListResponseByKey(responseBody, LINE_NAME)).containsExactly("분당선", "타르코프");
        assertThat(findListResponseByKey(responseBody, COLOR)).containsExactly("red", "yellow");

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findAllLineByName() {
        //give
        ExtractableResponse<Response> saveResponse = createLine("분당선", "red", upStation.getId(), downStation.getId(), 10);
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long id = saveResponse.jsonPath().getLong(LINE_ID);
        //when
        ExtractableResponse<Response> findResponse = findById(id);
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        JsonPath responseBody = findResponse.jsonPath();
        assertThat(findResponseByKey(responseBody, LINE_NAME)).isEqualTo("분당선");
        assertThat(findResponseByKey(responseBody, COLOR)).isEqualTo("red");

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLineTest() {
        //give
        ExtractableResponse<Response> saveResponse = createLine("분당선", "red", upStation.getId(), downStation.getId(), 10);
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long id = saveResponse.jsonPath().getLong(LINE_ID);

        //when
        ExtractableResponse<Response> update = updateLine(id,"2호선","yellow");
        assertThat(update.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        ExtractableResponse<Response> findResponse = findById(id);
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath responseBody = findResponse.jsonPath();
        assertThat(findResponseByKey(responseBody, LINE_NAME)).isEqualTo("2호선");
        assertThat(findResponseByKey(responseBody, COLOR)).isEqualTo("yellow");

    }

    private ExtractableResponse<Response> updateLine(Long id, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put(LINE_NAME, name);
        params.put(COLOR, color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(LINE_MAIN_PATH + "/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllLine() {
        return RestAssured.given().log().all()
                .when().get(LINE_MAIN_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findById(Long id) {
        return RestAssured.given().log().all()
                .when().get(LINE_MAIN_PATH + "/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put(LINE_NAME, name);
        params.put(COLOR, color);
        params.put(DISTANCE, distance);
        params.put(UP_STATION, upStationId);
        params.put(DOWN_STATION, downStationId);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_MAIN_PATH)
                .then().log().all()
                .extract();
    }

    private List<String> findListResponseByKey(JsonPath jsonPath, String key) {
        return jsonPath.getList(key, String.class);
    }

    private String findResponseByKey(JsonPath jsonPath, String key) {
        return jsonPath.get(key);
    }
}
