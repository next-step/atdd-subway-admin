package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class StationTestApi {
	public static ExtractableResponse<Response> 지하철_역_등록_요청(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		return RestAssured.given().log().all()
			  .body(params)
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .when()
			  .post("/stations")
			  .then().log().all()
			  .extract();
	}

	public static ExtractableResponse<Response> 지하철_역_목록_조회() {
		return RestAssured.given().log().all()
			  .when()
			  .get("/stations")
			  .then().log().all()
			  .extract();
	}

	public static ExtractableResponse<Response> 지하철_역_삭제(String uri) {
		return RestAssured.given().log().all()
			  .when()
			  .delete(uri)
			  .then().log().all()
			  .extract();
	}
}
