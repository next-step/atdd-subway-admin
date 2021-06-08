package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.LineTestData;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.common.StationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private LineTestData INCHEON_SUBWAY_LINE_1;
    private LineTestData INCHEON_SUBWAY_LINE_2;

    @BeforeEach
    void setUpTestData() {

        INCHEON_SUBWAY_LINE_1 = new LineTestData(
            "인천 1호선", "#7CA8D5", GYEYANG.toResponse(), GYULHYEON.toResponse()
        );

        INCHEON_SUBWAY_LINE_2 = new LineTestData(
            "인천 2호선", "#ED8B00", GEOMDAN_ORYU.toResponse(), WANGGIL.toResponse()
        );

        createAllStations();
    }

    @DisplayName("지하철 노선 생성")
    @TestFactory
    Stream<DynamicTest> createLineRequestTest() {
        return Stream.of(
            dynamicTest("인천 1호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_1))
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLineRequestTest02() {
        return Stream.of(
            dynamicTest("인천 1호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("인천 1호선 노선 다시 생성 시 실패", () -> {
                ExtractableResponse<Response> response = createLineRequest(INCHEON_SUBWAY_LINE_1);

                // then
                // 지하철_노선_생성_실패됨
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.header("Location")).isBlank();
            })
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> findLinesTest() {
        return Stream.of(
            dynamicTest("인천 1호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("인천 2호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_2)),
            dynamicTest("지하철 노선 목록 조회 및 검증", () ->
                fineLinesSuccess(INCHEON_SUBWAY_LINE_1, INCHEON_SUBWAY_LINE_2))
        );
    }

    @DisplayName("존재하지 않는 노선 번호로 노선을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLineFailTest() {
        return Stream.of(
            dynamicTest("인천 1호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("지하철 노선 조회 요청", () -> {
                ExtractableResponse<Response> response = findLine(100L);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            })
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @TestFactory
    Stream<DynamicTest> updateLineTest() {
        return Stream.of(
            dynamicTest("인천 1호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("인천 1호선 노선을 인천 2호선 노선으로 수정 및 검증", updateLineTo(INCHEON_SUBWAY_LINE_2))
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @TestFactory
    Stream<DynamicTest> deleteLineTest() {
        return Stream.of(
            dynamicTest("인천 1호선 노선 생성", createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("생성된 노선 삭제 및 검증", () -> {
                ExtractableResponse<Response> response = deleteLineRequest();
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            })
        );
    }

    private ExtractableResponse<Response> findSavedLine() {
        return RestAssured.given().log().all()
                          .when().get("/lines/1")
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> updateLineRequest(LineTestData data) {
        return RestAssured.given().log().all()
                          .body(data.getLine())
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().put("/lines/1")
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> deleteLineRequest() {
        return RestAssured.given().log().all()
                          .when().delete("/lines/1")
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> createLineRequest(LineTestData data) {
        return RestAssured.given().log().all()
                          .body(data.getLine())
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines")
                          .then().log().all()
                          .extract();
    }

    private Executable createLineRequestSuccess(LineTestData data) {
        return () -> {
            LineRequest lineRequest = data.getLine();
            ExtractableResponse<Response> response = createLineRequest(data);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).startsWith("/lines");

            assertThat(response.body().jsonPath().getString("name"))
                .isEqualTo(lineRequest.getName());

            assertThat(response.body().jsonPath().getString("color"))
                .isEqualTo(lineRequest.getColor());
        };
    }

    private void fineLinesSuccess(LineTestData data1, LineTestData data2) {
        // when
        // 지하철_노선_목록_조회_요청
        // when
        ExtractableResponse<Response> response = findLines();

        LineRequest line1 = data1.getLine();
        LineRequest line2 = data2.getLine();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<LineResponse> lines = response.body().jsonPath().getList("$", LineResponse.class);
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).extracting(LineResponse::getName)
                         .contains(line1.getName(), line2.getName());
        assertThat(lines).extracting(LineResponse::getColor)
                         .contains(line1.getColor(), line2.getColor());

        assertThat(lines.get(0).getStations()).hasSameElementsAs(data1.getStations());
        assertThat(lines.get(1).getStations()).hasSameElementsAs(data2.getStations());
    }

    private ExtractableResponse<Response> findLines() {
        return findLine(null);
    }

    private ExtractableResponse<Response> findLine(Long lineId) {

        String additionalPath = "";
        if (lineId != null) {
            additionalPath += lineId;
        }

        return RestAssured.given().log().all()
                          .when().get("/lines/" + additionalPath)
                          .then().log().all()
                          .extract();
    }

    private Executable updateLineTo(LineTestData data) {
        return () -> {
            ExtractableResponse<Response> response = updateLineRequest(data);
            ExtractableResponse<Response> actual = findSavedLine();

            // then
            // 지하철_노선_수정됨
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            LineRequest line = data.getLine();

            assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual.body().jsonPath().getString("name"))
                .isEqualTo(line.getName());
            assertThat(actual.body().jsonPath().getString("color"))
                .isEqualTo(line.getColor());
        };
    }
}
