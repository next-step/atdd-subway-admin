package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.error.ErrorResponse;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.application.LineService.LINE_DUPLICATED_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceStep {
    public static final String RESOURCES = "/lines";
    public static final String LOCATION = "Location";
    public static final String NAME = "name";
    public static final String COLOR = "color";

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put(NAME, name);
        params.put(COLOR, color);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RESOURCES)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        return 지하철_노선_생성_요청(name, color);
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getObject("", ErrorResponse.class).getMessage())
                .isEqualTo(LINE_DUPLICATED_EXCEPTION_MESSAGE);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get(RESOURCES)
                .then().log().all().extract();
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> expected1, ExtractableResponse<Response> expected2,
                                     ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Stream.of(expected1, expected2)
                .map(extractableResponse -> Long.parseLong(extractableResponse.header(LOCATION).split("/")[2])) //TODO: 더좋은 방법이 있을듯..?
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> expected) {
        return RestAssured
                .given().log().all()
                .when().get(expected.header(LOCATION))
                .then().log().all().extract();
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> expected, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject("", LineResponse.class)).isEqualTo(
                expected.jsonPath().getObject("", LineResponse.class));
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> givenResponse,
                                                             String newName, String newColor) {
        Map<String, String> params = new HashMap<>();
        params.put("name", newName);
        params.put("color", newColor);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(givenResponse.header(LOCATION))
                .then().log().all().extract();
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> givenResponse) {
        return RestAssured
                .given().log().all()
                .when().delete(givenResponse.header(LOCATION))
                .then().log().all().extract();
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
