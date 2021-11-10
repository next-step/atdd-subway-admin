package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class LineStep {

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest);
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
