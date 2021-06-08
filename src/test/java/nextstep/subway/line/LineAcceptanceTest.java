package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private StationAcceptanceTest stationAcceptanceTest;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @BeforeEach
    public void setup() {
        stationAcceptanceTest = new StationAcceptanceTest();
        name = "4호선";
        color = "blue";
        upStationId = getStationId("서울역");
        downStationId = getStationId("안산역");
        distance = 5;
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_생성(name, color, upStationId, downStationId, distance);

        //then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //when
        지하철_노선_생성(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> response = 지하철_노선_생성(name, color, upStationId, downStationId, distance);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성("2호선", "green", getStationId("강남역"), getStationId("잠실역"), 10);

        //when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        String createdLocationUri = 지하철_노선_등록되어_있음();

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회(createdLocationUri);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(createdLocationUri.split("/")[2]);
    }

    @DisplayName("지하철 노선을 조회 실패")
    @Test
    void getLineFail() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/1");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        String changeName = "2호선";
        String changeColor = "green";

        //when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(changeName, changeColor);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(changeName);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(changeColor);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        //given
        String createdLocationUri = 지하철_노선_등록되어_있음();

        //when
        ExtractableResponse<Response> response = 지하철_노선_삭제(createdLocationUri);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(지하철_노선_조회(createdLocationUri).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest line = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String createdLocationUri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(createdLocationUri)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private String 지하철_노선_등록되어_있음() {
        return 지하철_노선_생성(name, color, upStationId, downStationId, distance).header("Location");
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String changeName, String changeColor) {
        LineRequest line = new LineRequest(changeName, changeColor);
        String createdLocationUri = 지하철_노선_등록되어_있음();

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(line)
                .when()
                .put(createdLocationUri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(String createdLocationUri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(createdLocationUri)
                .then().log().all()
                .extract();
    }

    private long getStationId(String stationName) {
        return Long.parseLong(stationAcceptanceTest.지하철_역_생성되어_있음(stationName).split("/")[2]);
    }

}
