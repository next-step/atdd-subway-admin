package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.AbstractAcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.AcceptanceUtils.assertStatusCode;
import static nextstep.subway.AcceptanceUtils.extractName;
import static nextstep.subway.AcceptanceUtils.extractNames;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 서초역;
    private StationResponse 교대역;
    private StationResponse 강남역;

    @BeforeEach
    public void setUp() {
        super.setUp();
        서초역 = StationAcceptanceTest.createStation("서초역").extract().as(StationResponse.class);
        교대역 = StationAcceptanceTest.createStation("교대역").extract().as(StationResponse.class);
        강남역 = StationAcceptanceTest.createStation("강남역").extract().as(StationResponse.class);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void requestApiByCreateLine() {
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 서초역.getId(), 교대역.getId(), 10);

        ValidatableResponse response = requestApiByCreateLine(request);

        assertStatusCode(response, HttpStatus.CREATED);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void findAllLines () {
        requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 서초역.getId(), 교대역.getId(), 10));
        requestApiByCreateLine(new LineRequest("분당선", "bg-red-600", 서초역.getId(), 강남역.getId(), 10));

        ValidatableResponse response = requestApiByFindAllLines();

        assertThat(extractNames(response)).containsAnyOf("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLine () {
        Long lineId = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 서초역.getId(), 교대역.getId(), 10))
            .extract()
            .as(LineResponse.class)
            .getId();

        ValidatableResponse response = requestApiByGetLine(lineId);

        assertThat(extractName(response)).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine () {
        Long lineId = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 서초역.getId(), 교대역.getId(), 10))
            .extract()
            .as(LineResponse.class)
            .getId();

        ValidatableResponse response = requestApiByUpdateLine(lineId, new LineUpdateRequest("다른분당선", "bg-red-600"));

        assertStatusCode(response, HttpStatus.OK);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("노선 정보를 삭제한다.")
    @Test
    void deleteLine () {
        Long lineId = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 서초역.getId(), 교대역.getId(), 10))
            .extract()
            .as(LineResponse.class)
            .getId();

        ValidatableResponse response = requestApiByDeleteLine(lineId);

        assertStatusCode(response, HttpStatus.NO_CONTENT);
        assertThat(extractNames(requestApiByFindAllLines())).doesNotContain("신분당선");
    }

    public static ValidatableResponse requestApiByCreateLine(LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();
    }

    private static ValidatableResponse requestApiByFindAllLines() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all();
    }

    private static ValidatableResponse requestApiByGetLine(long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/{id}", id)
            .then().log().all();
    }

    private static ValidatableResponse requestApiByUpdateLine(long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
            .body(lineUpdateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", id)
            .then().log().all();
    }

    private static ValidatableResponse requestApiByDeleteLine(long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/{id}", id)
            .then().log().all();
    }

}
