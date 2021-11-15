package nextstep.subway.testFactory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AcceptanceTestFactory {

	public static Map<String, String> getNameAndColorContent(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		return params;
	}

	public static ExtractableResponse<Response> post(Map<String, String> params, String path) {
		return postBase(preBase(params).post(path));
	}

	public static ExtractableResponse<Response> get(String path) {
		return postBase(preBase().get(path));
	}

	public static ExtractableResponse<Response> put(Map<String, String> params, String path) {
		return postBase(preBase(params).put(path));
	}

	public static ExtractableResponse<Response> delete(String path) {
		return postBase(preBase().delete(path));
	}

	public static String getIdFromHeaderLocation(ExtractableResponse<Response> response){
		return response.header("Location").split("/")[2];
	}

	private static RequestSpecification preBase(Map<String, String> params){
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when();
	}

	private static RequestSpecification preBase(){
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when();
	}

	private static ExtractableResponse<Response> postBase(Response response){
		return response
			.then().log().all()
			.extract();
	}
}

