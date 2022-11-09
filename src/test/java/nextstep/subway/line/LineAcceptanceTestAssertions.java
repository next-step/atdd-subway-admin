package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LineAcceptanceTestAssertions {

    static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).startsWith("/lines/");
    }

}
