package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.AcceptanceTest.ID_POSITION;
import static nextstep.subway.station.StationStepTest.지하철_역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

public class LineStepTest {

    public static final String BASE_LINE_URL = "/lines";

    static void 지하철_구간_등록되어_있음(long lineId, SectionRequest request) {
        지하철_노선에_구간_추가_요청(lineId, request);
    }

    static long 지하철_노선_등록되어_있음(LineRequest line) {
        ExtractableResponse<Response> createdFirstLine = 지하철_노선_생성_요청(line);
        return extractIdByLocationHeader(createdFirstLine);
    }

    static SectionRequest 지하철_구간에_역들이_등록되어_있음(String upStationName, String downStationName, long distance) {
        long upStationId = 지하철_역_등록되어_있음(new StationRequest(upStationName));
        long downStationId = 지하철_역_등록되어_있음(new StationRequest(downStationName));
        return new SectionRequest(upStationId, downStationId, distance);
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_LINE_URL)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_노선_조회_요청(long createdId) {
        return RestAssured
                .given().log().all()
                .when().get(BASE_LINE_URL + "/" + createdId)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get(BASE_LINE_URL)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_노선_수정_요청(long createdId, LineRequest parameter) {
        return RestAssured
                .given().log().all()
                .body(parameter)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(BASE_LINE_URL + "/" + createdId)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_노선_제거_요청(long lineId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(BASE_LINE_URL + "/" + lineId)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_노선에_구간_추가_요청(long lineId, SectionRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_LINE_URL + "/" + lineId + "/sections")
                .then().log().all().extract();
    }

    static long extractIdByLocationHeader(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[ID_POSITION]);
    }

    static void 지하철_노선_같음(long createdId, ExtractableResponse<Response> response) {
        assertThat(createdId).isEqualTo(response.body().as(LineResponse.class).getId());
    }

    static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, Long...ids) {
        LinesResponse lines = response.body().as(LinesResponse.class);
        List<Long> resultIds = lines.getLines().stream()
                .map(line -> line.getId())
                .collect(Collectors.toList());

        assertThat(resultIds).containsAll(List.of(ids));
    }

    static void 노선_구간추가_성공_응답됨(ExtractableResponse<Response> response) {
        성공_상태_응답(response);
    }

    static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        성공_상태_응답(response);
    }

    static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        성공_상태_응답(response);
    }

    private static void 성공_상태_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    static void 구간_추가_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
