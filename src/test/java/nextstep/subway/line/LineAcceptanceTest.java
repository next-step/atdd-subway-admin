package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
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

    private long _1호선_상행역_Id;
    private long _1호선_하행역_Id;
    private long _2호선_상행역_Id;
    private long _2호선_하행역_Id;
    private long _3호선_상행역_Id;
    private long _3호선_하행역_Id;
    private String _1호선_이름 = "1호선";
    private String _1호선_색상 = "BLUE";
    private String _2호선_이름 = "2호선";
    private String _2호선_색상 = "GREEN";
    private String _3호선_이름 = "3호선";
    private String _3호선_색상 = "ORANGE";

    @BeforeEach
    void beforeEach() {
        _1호선_상행역_Id = 지하철역_생성_요청후_Id("청량리역");
        _1호선_하행역_Id = 지하철역_생성_요청후_Id("천호역");
        _2호선_상행역_Id = 지하철역_생성_요청후_Id("홍대역");
        _2호선_하행역_Id = 지하철역_생성_요청후_Id("잠실역");
        _3호선_상행역_Id = 지하철역_생성_요청후_Id("대화역");
        _3호선_하행역_Id = 지하철역_생성_요청후_Id("오금역");
    }

    @DisplayName("지하철 노선 관련 생성/조회/수정/삭제 통합 테스트")
    @Test
    void 지하철_노선_관련_통합_테스트() {
        // 지하철 노선을 생성한다.
        // When
        ExtractableResponse<Response> _1호선 = 지하철_노선_생성_요청(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        ExtractableResponse<Response> _2호선 = 지하철_노선_생성_요청(_2호선_이름, _2호선_색상, _2호선_상행역_Id, _2호선_하행역_Id, 1000L);
        ExtractableResponse<Response> _3호선 = 지하철_노선_생성_요청(_3호선_이름, _3호선_색상, _3호선_상행역_Id, _3호선_하행역_Id, 1500L);
        // Then
        지하철_노선_정보_확인(_1호선, _1호선_이름, _1호선_색상, Arrays.asList(_1호선_상행역_Id, _1호선_하행역_Id));
        지하철_노선_정보_확인(_2호선, _2호선_이름, _2호선_색상, Arrays.asList(_2호선_상행역_Id, _2호선_하행역_Id));
        지하철_노선_정보_확인(_3호선, _3호선_이름, _3호선_색상, Arrays.asList(_3호선_상행역_Id, _3호선_하행역_Id));

        // 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
        // When
        ExtractableResponse<Response> 재생성_1호선 = 지하철_노선_생성_요청(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        // Then
        지하철_노선_생성_실패됨(재생성_1호선);

        // 지하철 노선 목록을 조회한다.
        // When
        ExtractableResponse<Response> 지하철_노선_목록 = 지하철_노선_목록_조회_요청();
        // Then
        지하철_노선_목록_응답됨(지하철_노선_목록);
        지하철_노선_목록_포함됨(Arrays.asList(_1호선, _2호선, _3호선), 지하철_노선_목록);

        // 지하철 노선을 조회한다.
        // When
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(_1호선);
        // Then
        지하철_노선_응답됨(_1호선, response);

        // 지하철 노선을 수정한다.
        // When
        ExtractableResponse<Response> 수정된_1호선 = 지하철_노선_수정_요청(_1호선, _1호선_이름, _1호선_색상);
        // Then
        지하철_노선_수정됨(수정된_1호선, _1호선_이름, _1호선_색상);

        // 지하철 노선을 제거한다.
        // When
        ExtractableResponse<Response> 삭제된_1호선 = 지하철_노선_제거_요청(_1호선);
        // Then
        지하철_노선_삭제됨(삭제된_1호선);
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

    private void 지하철_노선_정보_확인(ExtractableResponse<Response> response, String name, String color, List<Long> upAndDownStationId) {
        지하철_노선_생성됨(response);
        final LineResponse createdObject = response.body().as(LineResponse.class);
        assertAll(
                () -> assertThat(createdObject.getName()).isEqualTo(name),
                () -> assertThat(createdObject.getColor()).isEqualTo(color),
                () -> assertThat(createdObject.getStations()).hasSize(upAndDownStationId.size()),
                () -> assertThat(createdObject.getStations())
                        .extracting(StationResponse::getId)
                        .containsExactlyElementsOf(upAndDownStationId)
                        .containsSequence(upAndDownStationId)
        );
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_목록_포함됨(List<ExtractableResponse<Response>> responses, ExtractableResponse<Response> response) {
        List<LineResponse> resultLines = new ArrayList<>(response.jsonPath().getList(".", LineResponse.class));
        List<Long> expectedLineIds = responses.stream()
                .map(it -> Long.parseLong(convertResourceId(it)))
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

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        String urlPath = convertLocationUrl(response);
        return RestAssured
                .given().log().all()
                .when().get(urlPath)
                .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> createdResponse, ExtractableResponse<Response> response) {
        요청_완료(response);
        String expected = convertResourceId(createdResponse);
        assertAll(
                () -> assertThat(String.valueOf((int) response.jsonPath().get("id"))).isEqualTo(expected),
                () -> assertNotNull(response.jsonPath().get("name")),
                () -> assertNotNull(response.jsonPath().get("color")),
                () -> assertNotNull(response.jsonPath().get("stations"))
        );
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createdResponse, String name, String color) {
        String createdUrl = convertLocationUrl(createdResponse);
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createLineRequest(name, color))
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

    private ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createdResponse) {
        String createdUrl = convertLocationUrl(createdResponse);
        return RestAssured
                .given().log().all()
                .when().delete(createdUrl)
                .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        컨텐츠_생성됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        서버_내부_에러(response);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        요청_완료(response);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        컨텐츠_없음(response);
    }

    private String convertResourceId(ExtractableResponse<Response> createdResponse) {
        return convertLocationUrl(createdResponse).split("/")[2];
    }

    private String convertLocationUrl(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private static LineRequest createLineRequest(String name, String color, long upStationId, long downStationId, long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private static LineRequest createLineRequest(String name, String color) {
        return new LineRequest(name, color);
    }
}
