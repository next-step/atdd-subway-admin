package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceStep {
    public static ExtractableResponse<Response> NEW_LINE_ALREADY_CREATED(
            final String lineName, final String lineColor
    ) {
        return REQUEST_CREATE_NEW_LINE(lineName, lineColor);
    }

    public static ExtractableResponse<Response> REQUEST_CREATE_NEW_LINE(
            final String lineName, final String lineColor
    ) {
        LineRequest lineRequest = new LineRequest(lineName, lineColor);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static void NEW_LINE_CREATED(
            final ExtractableResponse<Response> response, final String lineName, final String lineColor
    ) {
        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotEmpty();
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
        assertThat(lineResponse.getName()).isEqualTo(lineName);
    }
}
