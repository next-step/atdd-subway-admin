package nextstep.subway.api;

import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static nextstep.subway.api.HttpMethod.지하철_노선_조회;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AssertMethod {
    public static void 지하철_노선_생성_확인(ExtractableResponse<Response> response, Map<String, String> params) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank(),
            () -> assertThat(response.as(LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor, e -> e.getStations().get(0).getId(), e -> e.getStations().get(1).getId())
                .contains(params.get("name"), params.get("color"), Long.valueOf(params.get("upStationId")), Long.valueOf(params.get("downStationId")))
        );
    }

    public static void 지하철_노선_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_목록_포함_확인(ExtractableResponse response, LineResponse lineResponse) {
        List<LineResponse> lineResponses = response.as(new TypeRef<List<LineResponse>>() {
        });

        assertThat(lineResponses)
            .extracting(LineResponse::getId, LineResponse::getColor, LineResponse::getName)
            .contains(tuple(lineResponse.getId(), lineResponse.getColor(), lineResponse.getName()));
    }

    public static void 지하철_노선_정상_응답_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_포함_확인(ExtractableResponse response, LineResponse lineResponse) {
        assertThat(response.as(LineResponse.class))
            .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
            .contains(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

    public static void 지하철_노선_수정_확인(ExtractableResponse response, Map<String, String> params) {
        assertThat(response.as(LineResponse.class))
            .extracting(LineResponse::getName, LineResponse::getColor)
            .contains(params.get("name"), params.get("color"));
    }

    public static void 지하철_노선_삭제_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_조회_없음_확인(ExtractableResponse response) {
        assertThat(지하철_노선_조회(response).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
