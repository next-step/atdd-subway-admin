package nextstep.subway.line.accecptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceResponse {
    public static void 지하철노선_생성_성공(ExtractableResponse<Response> response) {
        String lineName = response.jsonPath().get("name");
        assertThat(lineName).isEqualTo("2호선");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철노선_목록_조회_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(".")).hasSize(2);
    }

    public static void 지하철노선_조회_성공(ExtractableResponse<Response> response) {
        String lineName = response.jsonPath().get("name");
        assertThat(lineName).isEqualTo("2호선");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_조회_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static void 지하철노선_수정_조회_성공(ExtractableResponse<Response> response, String updatedName) {
        String lineName = response.jsonPath().get("name");
        assertThat(lineName).isEqualTo(updatedName);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_수정_조회_실패(ExtractableResponse<Response> response, String updatedName) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
