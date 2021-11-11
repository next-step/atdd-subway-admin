package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final int GANGNAM_YEOKSAM_DISTANCE = 10;

    private static StationResponse gangnamStation;
    private static StationResponse yeoksamStation;

    @BeforeEach
    void beforeEach() {
        gangnamStation = 지하철_역_생성("강남역");
        yeoksamStation = 지하철_역_생성("역삼역");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String firstLineName = "1호선";
        String blueColor = "blue";

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(firstLineName, blueColor,
            gangnamStation.getId(), yeoksamStation.getId(), GANGNAM_YEOKSAM_DISTANCE);

        // then
        지하철_노선_생성됨(response, firstLineName, blueColor);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_duplicateName_400() {
        // given
        String firstLineName = "1호선";
        String blueColor = "blue";
        지하철_노선_등록되어_있음(firstLineName, blueColor, gangnamStation.getId(), yeoksamStation.getId(),
            GANGNAM_YEOKSAM_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(
            firstLineName, blueColor, gangnamStation.getId(), yeoksamStation.getId(),
            GANGNAM_YEOKSAM_DISTANCE);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 값으로 지하철 노선을 생성할 수 없다.")
    @DisplayName("빈 값 존재하면 지하철 노선은 생성할 수 없다.")
    @CsvSource({",color,1,2,10", "name,,1,2,10",
        "name,color,,2,10", "name,color,1,,10", "name,color,1,2,"})
    void createLine_emptyParameter_400(String name, String color,
        Long upStationId, Long downStationId, Integer distance) {

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color,
            upStationId, downStationId, distance);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("존재하지 않는 역으로 노선을 생성한다.")
    @Test
    void createLine_notExistsStation_404() {
        // given, when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(
            "1호선", "blue", Long.MIN_VALUE, Long.MAX_VALUE, GANGNAM_YEOKSAM_DISTANCE);

        // then
        지하철_역_못찾음(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse firstLineResponse = 지하철_노선_등록되어_있음("1호선", "blue",
            gangnamStation.getId(), yeoksamStation.getId(), GANGNAM_YEOKSAM_DISTANCE)
            .as(LineResponse.class);
        LineResponse secondLineResponse = 지하철_노선_등록되어_있음("2호선", "green",
            gangnamStation.getId(), yeoksamStation.getId(), GANGNAM_YEOKSAM_DISTANCE)
            .as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        //then
        assertAll(
            () -> 지하철_노선_목록_응답됨(response),
            () -> 지하철_노선_목록_포함됨(response, Arrays.asList(firstLineResponse, secondLineResponse))
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdFirstLineResponse = 지하철_노선_등록되어_있음(
            "1호선", "blue", gangnamStation.getId(), yeoksamStation.getId(),
            GANGNAM_YEOKSAM_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdFirstLineResponse);

        // then
        지하철_노선_응답됨(response, createdFirstLineResponse.as(LineResponse.class));
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_notExistsLine_404() {
        //given, when
        ExtractableResponse<Response> response = 존재하지_않는_지하철_노선_조회_요청();

        // then
        지하철_노선_못찾음(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음(
            "1호선", "blue", gangnamStation.getId(), yeoksamStation.getId(),
            GANGNAM_YEOKSAM_DISTANCE);
        String updatedSecondLineName = "2호선";
        String updatedRedColor = "red";

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdResponse,
            updatedSecondLineName, updatedRedColor);

        // then
        지하철_노선_수정됨(response, createdResponse, updatedSecondLineName, updatedRedColor);
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 값으로 수정할 수 없다.")
    @DisplayName("이름 또는 색상을 빈값으로 지하철 노선을 수정한다.")
    @CsvSource({",color", "name,"})
    void updateLine_emptyNameOrColor_400(String updatedName, String updatedColor) {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음(
            "1호선", "blue", gangnamStation.getId(), yeoksamStation.getId(),
            GANGNAM_YEOKSAM_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdResponse,
            updatedName, updatedColor);

        // then
        지하철_노선_수정_실패됨(response);
    }

    @Test
    @DisplayName("기존에 존재하는 지하철 호선으로 변경할 수 없다.")
    void updateLine_duplicationName_400() {
        // given
        String existsName = "1호선";
        지하철_노선_등록되어_있음(existsName, "blue",
            gangnamStation.getId(), yeoksamStation.getId(), GANGNAM_YEOKSAM_DISTANCE);

        ExtractableResponse<Response> secondLineResponse = 지하철_노선_등록되어_있음(
            "2호선", "green", gangnamStation.getId(), yeoksamStation.getId(),
            GANGNAM_YEOKSAM_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(secondLineResponse,
            existsName, "any");

        // then
        지하철_노선_수정_실패됨(response);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateLine_notExistsLine_404() {
        //when
        ExtractableResponse<Response> response = 존재하지_않는_지하철_노선_수정_요청();

        // then
        지하철_노선_못찾음(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음("1호선", "blue",
            gangnamStation.getId(), yeoksamStation.getId(), GANGNAM_YEOKSAM_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdResponse);

        // then
        지하철_노선_삭제됨(response, createdResponse);
    }

    private Map<String, String> createdBody(String name, String color,
        Long upStationId, Long downStationId, Integer distance) {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        body.put("upStationId", String.valueOf(upStationId));
        body.put("downStationId", String.valueOf(downStationId));
        body.put("distance", String.valueOf(distance));
        return body;
    }

    private StationResponse 지하철_역_생성(String name) {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Collections.singletonMap("name", name))
            .when()
            .post("/stations")
            .then()
            .extract()
            .as(StationResponse.class);
    }

    private String headerLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineResponse expectedLine) {
        LineResponse line = response.as(LineResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(line.getId()).isNotNull(),
            () -> assertThat(line.getName()).isEqualTo(expectedLine.getName()),
            () -> assertThat(line.getColor()).isEqualTo(expectedLine.getColor()),
            () -> assertThat(line.getStations())
                .hasSize(2)
                .extracting(StationResponse::getId, StationResponse::getName)
                .containsExactly(
                    tuple(gangnamStation.getId(), gangnamStation.getName()),
                    tuple(yeoksamStation.getId(), yeoksamStation.getName())
                )
        );
    }

    private void 지하철_역_못찾음(ExtractableResponse<Response> response) {
        찾을_수_없음(response);
    }

    private void 지하철_노선_못찾음(ExtractableResponse<Response> response) {
        찾을_수_없음(response);
    }

    private AbstractIntegerAssert<?> 찾을_수_없음(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철_노선_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", Long.MIN_VALUE)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response,
        ExtractableResponse<Response> createdResponse,
        String expectedName, String expectedColor) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(지하철_노선_조회_요청(createdResponse).as(LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsExactly(expectedName, expectedColor)
        );
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(
        ExtractableResponse<Response> createdResponse,
        String name, String color) {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(updatedBody(name, color))
            .when()
            .put(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철_노선_수정_요청() {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(updatedBody("1호선", "blue"))
            .when()
            .put("/lines/{id}", Integer.MIN_VALUE)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(
        ExtractableResponse<Response> createdResponse) {
        return RestAssured.given().log().all()
            .when()
            .get(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private AbstractIntegerAssert<?> 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode())
            .isEqualTo(HttpStatus.OK.value());
    }

    private AbstractListAssert<?, List<? extends Tuple>, Tuple, ObjectAssert<Tuple>> 지하철_노선_목록_포함됨(
        ExtractableResponse<Response> response, List<LineResponse> expectedLines) {
        List<LineResponse> lineResponses = response.as(new TypeRef<List<LineResponse>>() {
        });
        return assertThat(lineResponses)
            .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
            .containsExactly(
                expectedLines.stream()
                    .map(line -> tuple(line.getId(), line.getName(), line.getColor()))
                    .toArray(Tuple[]::new)
            );
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        잘못된_요청(response);
    }

    private void 지하철_노선_수정_실패됨(ExtractableResponse<Response> response) {
        잘못된_요청(response);
    }

    private AbstractIntegerAssert<?> 잘못된_요청(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response, String expectedName,
        String expectedColor) {
        LineResponse line = response.as(LineResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(headerLocation(response)).isNotBlank(),
            () -> assertThat(line.getId()).isNotNull(),
            () -> assertThat(line.getName()).isEqualTo(expectedName),
            () -> assertThat(line.getColor()).isEqualTo(expectedColor),
            () -> assertThat(line.getCreatedDate()).isNotNull(),
            () -> assertThat(line.getModifiedDate()).isNotNull(),
            () -> assertThat(line.getStations())
                .hasSize(2)
                .extracting(StationResponse::getId, StationResponse::getName)
                .containsExactly(
                    tuple(gangnamStation.getId(), gangnamStation.getName()),
                    tuple(yeoksamStation.getId(), yeoksamStation.getName())
                ));
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color,
        Long upStationId, Long downStationId, Integer distance) {
        return RestAssured.given().log().all()
            .body(createdBody(name, color, upStationId, downStationId, distance))
            .contentType(ContentType.JSON)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color,
        long upStationId, long downStationId, int distance) {
        return RestAssured.given()
            .body(createdBody(name, color, upStationId, downStationId, distance))
            .contentType(ContentType.JSON)
            .post("/lines")
            .then()
            .extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response,
        ExtractableResponse<Response> createdResponse) {
        assertAll(
            () -> assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(지하철_노선_조회_요청(createdResponse).statusCode())
                .isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(
        ExtractableResponse<Response> createdResponse) {
        return RestAssured.given().log().all()
            .when()
            .delete(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private Map<String, String> updatedBody(String name, String color) {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        return body;
    }
}
