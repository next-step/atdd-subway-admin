package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;


public class LineAcceptanceTestSteps {

	private static final String URL_BASE = "/lines";
	private static final String URL_GET_LINES = URL_BASE;
	private static final String URL_CREATE_LINE = URL_BASE;
	private static final String URL_GET_LINE = URL_BASE + "/%d";
	private static final String URL_UPDATE_LINE = URL_BASE + "/%d";
	private static final String URL_DELETE_LINE = URL_BASE + "/%d";

	static ExtractableResponse<Response> send_createLine(final LineRequest request) {
		// when
		return RestAssured.given().log().all()
						  .body(request)
						  .contentType(MediaType.APPLICATION_JSON_VALUE)
						  .when().post(URL_CREATE_LINE)
						  .then().log().all().extract();
	}

	static ExtractableResponse<Response> send_getLines() {
		// when
		return RestAssured.given().log().all()
						  .when().get(URL_GET_LINES)
						  .then().log().all().extract();
	}

	static ExtractableResponse<Response> send_getLine(final Long id) {
		String url = String.format(URL_GET_LINE, id);

		// when
		return RestAssured.given().log().all()
						  .when().get(url)
						  .then().log().all().extract();
	}

	static ExtractableResponse<Response> send_updateLine(final Long id, final LineRequest request) {
		String url = String.format(URL_UPDATE_LINE, id);

		// when
		return RestAssured.given().log().all()
						  .body(request)
						  .contentType(MediaType.APPLICATION_JSON_VALUE)
						  .when().put(url)
						  .then().log().all().extract();
	}

	static ExtractableResponse<Response> send_deleteLine(final Long id) {
		String url = String.format(URL_DELETE_LINE, id);

		// when
		return RestAssured.given().log().all()
						  .when().delete(url)
						  .then().log().all().extract();
	}
}
