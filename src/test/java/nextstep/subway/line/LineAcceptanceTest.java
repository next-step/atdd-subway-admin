package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTestRequest.지하철역_생성_요청후_Id;
import static nextstep.subway.utils.HttpTestStatusCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("2호선", "GREEN", 1L, 2L, 1000L);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "2호선";
        String lineColor = "GREEN";
        long upStationId = 1L;
        long downStationId = 2L;
        long distance = 1000L;
        지하철_노선_생성_요청(lineName, lineColor, upStationId, downStationId, distance);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineName, lineColor, upStationId, downStationId, distance);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        List<String> createdUrls = Arrays.asList(
                지하철_노선_등록되어_있음("2호선", "GREEN", 1L, 2L, 1000L),
                지하철_노선_등록되어_있음("1호선", "BLUE", 3L, 4L, 500L)
        );

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(createdUrls, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        String createdUrl = 지하철_노선_등록되어_있음("2호선", "GREEN", 1L, 2L, 1000L);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdUrl);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(createdUrl, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        String createdUrl = 지하철_노선_등록되어_있음("2호선", "GREEN", 1L, 2L, 1000L);

        // when
        // 지하철_노선_수정_요청
        String name = "1호선";
        String color = "BLUE";
        long upStationId = 지하철역_생성_요청후_Id("잠실역");
        long downStationId = 지하철역_생성_요청후_Id("역삼역");
        long distance = 1000L;
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdUrl, name, color, upStationId, downStationId, distance);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(response, name, color);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        String createdUrl = 지하철_노선_등록되어_있음("1호선", "BLUE", 1L, 2L, 1000L);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdUrl);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, long upStationId, long downStationId, long distance) {
        LineRequest request = createLineRequest(name, color, upStationId, downStationId, distance);
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        컨텐츠_생성됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        서버_내부_에러(response);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        요청_완료(response);
    }

    private void 지하철_노선_목록_포함됨(List<String> requestedUrls, ExtractableResponse<Response> response) {
        List<LineResponse> resultLines = new ArrayList<>(response.jsonPath().getList(".", LineResponse.class));
        List<Long> expectedLineIds = requestedUrls.stream()
                .map(it -> Long.parseLong(it.split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = resultLines.stream().map(LineResponse::getId).collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
        for (LineResponse resultLine : resultLines) {
            assertAll(
                    () -> assertNotNull(resultLine.getColor()),
                    () -> assertNotNull(resultLine.getName()),
                    () -> assertNotNull(resultLine.getStations())
            );
        }
    }

    private String 지하철_노선_등록되어_있음(String name, String color, long upStationId, long downStationId, long distance) {
        return 지하철_노선_생성_요청(name, color, upStationId, downStationId, distance).header("Location");
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
        return RestAssured
                .given().log().all()
                .when().get(uri)
                .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(String uri, ExtractableResponse<Response> response) {
        요청_완료(response);
        String expected = uri.split("/")[2];
        assertAll(
                () -> assertThat(String.valueOf((int) response.jsonPath().get("id"))).isEqualTo(expected),
                () -> assertNotNull(response.jsonPath().get("name")),
                () -> assertNotNull(response.jsonPath().get("color")),
                () -> assertNotNull(response.jsonPath().get("stations"))
        );
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String createdUrl, String name, String color, long upStationId, long downStationId, long distance) {
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createLineRequest(name, color, upStationId, downStationId, distance))
                .when().put(createdUrl)
                .then().log().all().extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, String name, String color) {
        요청_완료(response);
        assertAll(
                () -> assertEquals(response.jsonPath().getString("name"), name),
                () -> assertEquals(response.jsonPath().getString("color"), color)
        );
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String createdUrl) {
        return RestAssured
                .given().log().all()
                .when().delete(createdUrl)
                .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        컨텐츠_없음(response);
    }

    private static LineRequest createLineRequest(String name, String color, long upStationId, long downStationId, long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
}
