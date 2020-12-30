package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineTestApi {

	public static ExtractableResponse<Response> 지하철_노선_조회_요청(String path) {
		return RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .when().get(path)
			  .then().log().all()
			  .extract();
	}

	public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
		return RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .body(lineRequest)
			  .when().post("/lines")
			  .then().log().all()
			  .extract();
	}

	public static ExtractableResponse<Response> 지하철_노선_수정_요청(String path,
		  Map<String, String> params) {
		return RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .body(params)
			  .when().put(path)
			  .then().log().all()
			  .extract();
	}

	public static ExtractableResponse<Response> 지하철_노선_제거_요청(String path) {
		return RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .when().delete(path)
			  .then().log().all()
			  .extract();
	}
}
