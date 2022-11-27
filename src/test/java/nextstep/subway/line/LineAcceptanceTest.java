package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철호선 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    @AfterEach
    public void cleanUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("지하철호선 생성 테스트")
    void createLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = StationAcceptanceTest.createStationAndGetId("지하철역");
        Long downStationId = StationAcceptanceTest.createStationAndGetId("새로운지하철역");

        //when
        final ExtractableResponse<Response> apiResponse =
                createLine(new LineRequest(lineName,"bg-red-600",upStationId,downStationId,10));

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineName.equals(apiResponse.jsonPath().getObject("name",String.class)))
                .isTrue();
    }

    @Test
    @DisplayName("지하철호선 두개 생성 후 목록조회 테스트")
    void getLinesTest() {
        //given
        String lineName1 = "신분당선";
        String lineName2 = "분당선";
        Long stationId = StationAcceptanceTest.createStationAndGetId("지하철역");
        Long newStationId = StationAcceptanceTest.createStationAndGetId("새로운지하철역");
        Long anotherStationId = StationAcceptanceTest.createStationAndGetId("또다른지하철역");
        createLine(new LineRequest(lineName1, "bg-red-600", stationId, newStationId, 10));
        createLine(new LineRequest(lineName2, "bg-red-600", stationId, anotherStationId, 10));

        //when
        Set<String> results = new HashSet<>(retrieveAllLineNames());

        //then
        assertThat(results).containsOnly(lineName1, lineName2);
    }

    @Test
    @DisplayName("하나의 지하철호선을 생성 후 조회해 본다.")
    void retrieveOneLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = StationAcceptanceTest.createStationAndGetId("지하철역");
        Long downStationId = StationAcceptanceTest.createStationAndGetId("새로운지하철역");
        Long lineId = createLineAndGetId(
                new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 10));

        //when
        String resultLineName = retrieveLineName(lineId);

        //then
        assertThat(lineName.equals(resultLineName)).isTrue();
    }

    @Test
    @DisplayName("지하철호선 정보를 수정한다.")
    void updateLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = StationAcceptanceTest.createStationAndGetId("지하철역");
        Long downStationId = StationAcceptanceTest.createStationAndGetId("새로운지하철역");
        Long lineId = createLineAndGetId(
                new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 10));

        //when
        String newLineName = "다른분당선";
        String newColor = "bg-red-600";
        updateLine(lineId, new LineUpdateRequest(newLineName, newColor));

        //then
        LineResponse updatedLine = retrieveLineResponse(lineId);
        assertThat(newLineName.equals(updatedLine.getName()) && newColor.equals(updatedLine.getColor()))
                .isTrue();
    }

    @Test
    @DisplayName("지하철호선 정보를 삭제한다.")
    void deleteLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = StationAcceptanceTest.createStationAndGetId("지하철역");
        Long downStationId = StationAcceptanceTest.createStationAndGetId("새로운지하철역");
        Long lineId = createLineAndGetId(
                new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 10));

        //when
        int resultCode = deleteLine(lineId).statusCode();

        //then
        assertThat(resultCode).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * 지하철호선 생성
     */
    static ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        final Map param = new HashMap();
        param.put("name", lineRequest.getName());
        param.put("color", lineRequest.getColor());
        param.put("upStationId", lineRequest.getUpStationId());
        param.put("downStationId", lineRequest.getDownStationId());
        param.put("distance", lineRequest.getDistance());

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    /**
     * 지하철호선 생성 후 id 조회
     */
    public static Long createLineAndGetId(LineRequest lineRequest) {
        return Long.parseLong(createLine(lineRequest).jsonPath().get("id").toString());
    }

    /**
     * 지하철전체호선 목록조회
     */
    List<LineResponse> retrieveAllLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract().jsonPath()
                .getList("",LineResponse.class);
    }

    /**
     * 지하철전체호선 이름 목록조회
     */
    List<String> retrieveAllLineNames() {
        return retrieveAllLines().stream().map(LineResponse::getName).collect(Collectors.toList());
    }


    /**
     * 지하철호선id로 정보 조회
     */
    ExtractableResponse<Response> retrieveLine(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all().extract();
    }

    /**
     * 지하철호선id로 정보 조회
     */
    public static LineResponse retrieveLineResponse(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all().extract().jsonPath()
                .getObject("", LineResponse.class);
    }

    /**
     * 지하철호선 Id로 지하철호선 이름 조회
     */
    String retrieveLineName(Long lineId) {
        return retrieveLineResponse(lineId).getName();
    }

    /**
     * 지하철호선 정보 수정
     */
    void updateLine(Long lineId, LineUpdateRequest lineUpdateRequest) {
        final Map param = new HashMap();
        param.put("name", lineUpdateRequest.getName());
        param.put("color", lineUpdateRequest.getColor());

        RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all().extract();
    }

    /**
     * 지하철호선 정보 삭제
     */
    private ExtractableResponse<Response> deleteLine(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineId)
                .then().log().all().extract();
    }

}
