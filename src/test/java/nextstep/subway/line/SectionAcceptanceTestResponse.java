package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class SectionAcceptanceTestResponse {
	public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}
}
