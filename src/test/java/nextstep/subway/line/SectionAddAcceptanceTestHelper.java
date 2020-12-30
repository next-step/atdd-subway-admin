package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAddAcceptanceTestHelper {

    public static ExtractableResponse<Response> 노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, long distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all().
                body(sectionRequest).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/"+lineId+"/sections").
                then().
                log().all().
                extract();
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static LineRequest 노선_구간_요청_생성(String lineName, String lineColor, Long upStationId, Long downStationId, long distance) {
        return LineTestCommon.createLineParams(lineName, lineColor, upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> 노선_등록시_구간_등록되어_있음(LineRequest request) {
        return LineTestCommon.createResponse(request, "/lines");
    }

    public static ExtractableResponse<Response> 지하철역_등록되어_있음(String name) {
        return 지하철역_생성_요청(name);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                extract();
    }

    public static void 지하철_노선_구간_등록_예외_케이스로_등록되지_않음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
