package nextstep.subway.line;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;

public class LineTestFixture {
	public static final String EXAMPLE_LINE1_NAME = "1호선";
	public static final String EXAMPLE_LINE2_NAME = "2호선";
	public static final String EXAMPLE_LINE1_COLOR = "파란색";
	public static final String EXAMPLE_LINE2_COLOR = "녹색";
	public static final String LINE_URL_PREFIX = "/lines";

	public static ExtractableResponse<Response> requestCreateLine(LineRequest lineRequest) {
		return RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(LINE_URL_PREFIX)
			.then()
			.log()
			.all()
			.extract();
	}

	public static ExtractableResponse<Response> requestGetLines() {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(LINE_URL_PREFIX)
			.then()
			.log()
			.all()
			.extract();
	}

	public static ExtractableResponse<Response> requestGetLineById(Long id) {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(String.format("%s/%s", LINE_URL_PREFIX, id))
			.then()
			.log()
			.all()
			.extract();
	}
}
