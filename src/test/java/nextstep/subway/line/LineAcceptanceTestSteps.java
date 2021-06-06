package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineAcceptanceTestSteps {

	private static final String URL_CREATE_LINE = "/lines";

	static ExtractableResponse<Response> send_createLine(final LineRequest request) {
		// when
		return RestAssured
			.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post(URL_CREATE_LINE)
			.then().log().all().extract();
	}

	static ExtractableResponse<Response> send_getLines() {
		// when
		return RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();
	}
}
