package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTestHelper {

	private static final String URL_CREATE_LINE = "/lines";

	static LineRequest makeLineRequest(final String name, final String color) {
		return new LineRequest(name, color);
	}


	static ExtractableResponse<Response> send_createLine(final LineRequest request) {
		// when
		return RestAssured
			.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post(URL_CREATE_LINE)
			.then().log().all().extract();
	}

	static void send_createLine_with_success_check(final LineRequest request) {
		assertThat(send_createLine(request).statusCode())
			.isEqualTo(HttpStatus.CREATED.value());
	}
}
