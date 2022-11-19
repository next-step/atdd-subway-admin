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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Long upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));

        //when
        assertThat(createLineAndGetResponseCode(
                new LineRequest(lineName,"bg-red-600",upStationId,downStationId,10)))
                .isEqualTo(HttpStatus.CREATED.value());

        //then
        assertThat(retrieveAllLineNames().stream().anyMatch(lineName::equals))
                .isTrue();
    }

    @Test
    @DisplayName("지하철호선 두개 생성 후 목록조회 테스트")
    void getLinesTest() {
        //given
        String lineName1 = "신분당선";
        String lineName2 = "분당선";
        Long stationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long newStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));
        Long anotherStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("또다른지하철역"));
        createLine(new LineRequest(lineName1, "bg-red-600", stationId, newStationId, 10));
        createLine(new LineRequest(lineName2, "bg-red-600", stationId, anotherStationId, 10));

        //when
        List<String> lineNames = retrieveAllLineNames();

        //then
        assertThat(lineNames.size()).isEqualTo(2);
        assertThat(lineNames.stream()
                .allMatch(lineName -> lineName1.equals(lineName) || lineName2.equals(lineName)))
                .isTrue();
    }

    @Test
    @DisplayName("하나의 지하철호선을 생성 후 조회해 본다.")
    void retrieveOneLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));
        String lineId = createLineAndGetId(
                new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 10));

        //when
        //then
        assertThat(lineName.equals(retrieveLineName(lineId)))
                .isTrue();
    }

    @Test
    @DisplayName("지하철호선 정보를 수정한다.")
    void updateLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));
        String lineId = createLineAndGetId(
                new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 10));

        //when
        String newLineName = "다른분당선";
        String newColor = "bg-red-600";
        updateLine(lineId, new LineUpdateRequest(newLineName, newColor));

        //then
        LineResponse updatedLine = retrieveLine(lineId);
        assertThat(newLineName.equals(updatedLine.getName()) && newColor.equals(updatedLine.getColor()))
                .isTrue();
    }

    @Test
    @DisplayName("지하철호선 정보를 삭제한다.")
    void deleteLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));
        String lineId = createLineAndGetId(
                new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 10));

        //when
        deleteLine(lineId);

        //then
        assertThat(retrieveAllLines().size()).isEqualTo(0);
        assertThat(retrieveAllLineNames().stream().anyMatch(lineName::equals))
                .isFalse();
    }

    /**
     * 지하철호선 생성
     */
    ExtractableResponse<Response> createLine(LineRequest lineRequest) {
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
     * 지하철호선 생성 후 http상태코드 조회
     */
    int createLineAndGetResponseCode(LineRequest lineRequest) {
        return createLine(lineRequest).statusCode();
    }

    /**
     * 지하철호선 생성 후 id 조회
     */
    String createLineAndGetId(LineRequest lineRequest) {
        return createLine(lineRequest).jsonPath().get("id").toString();
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
     * 지하철호선id로 정 조회
     */
    LineResponse retrieveLine(String lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all().extract().jsonPath()
                .getObject("", LineResponse.class);
    }

    /**
     * 지하철호선 Id로 지하철호선 이름 조회
     */
    String retrieveLineName(String lineId) {
        return retrieveLine(lineId).getName();
    }

    /**
     * 지하철호선 정보 수정
     */
    void updateLine(String lineId, LineUpdateRequest lineUpdateRequest) {
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
    private void deleteLine(String lineId) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineId)
                .then().log().all().extract();
    }

}
