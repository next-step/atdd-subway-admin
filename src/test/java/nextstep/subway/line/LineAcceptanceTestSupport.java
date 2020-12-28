package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("NonAsciiCharacters")
class LineAcceptanceTestSupport {
	LineAcceptanceTestSupport() {
	}

	ExtractableResponse<Response> 지하철노선_얻기() {
		return RestAssured
				.given().log().all()
				.when().get("/lines")
				.then().log().all().extract();
	}

	void 지하철노선목록_조회_검사(ExtractableResponse<Response> getResponse,
	                   ExtractableResponse<Response>... createResponse) {
		Assertions.assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedStationIds = Arrays.stream(createResponse)
				.map(response -> response.header("Location").split("/")[2])
				.map(Long::parseLong)
				.collect(Collectors.toList());
		List<Long> actualStationIds = getResponse.jsonPath().getList(".", LineResponse.class).stream()
				.map(LineResponse::getId)
				.collect(Collectors.toList());
		Assertions.assertThat(expectedStationIds).containsAll(actualStationIds);
	}

	ExtractableResponse<Response> 지하철노선_조회(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	void 지하철노선_조회_검사(ExtractableResponse<Response> createResponse, ExtractableResponse<Response> getResponse) {
		Assertions.assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long expectedId = createResponse.body().as(LineResponse.class).getId();
		Long actualId = getResponse.body().as(LineResponse.class).getId();
		Assertions.assertThat(actualId).isEqualTo(expectedId);
	}

	ExtractableResponse<Response> 지하철노선_조회(String uri) {
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	void 지하철노선_조회_없음(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	ExtractableResponse<Response> 지하철노선_수정_요청(ExtractableResponse<Response> createResponse,
	                                          String name, String color) {
		String uri = createResponse.header("Location");

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("color", color);
		return RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().put(uri)
				.then().log().all().extract();
	}

	void 지하철노선_수정_검사(ExtractableResponse<Response> updateResponse) {
		Assertions.assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	ExtractableResponse<Response> 지하철노선_삭제_요청(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured.given().log().all()
				.when().delete(uri)
				.then().log().all().extract();
	}

	void 지하철노선_삭제_검사(ExtractableResponse<Response> deleteResponse) {
		Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("color", color);

		return RestAssured
				.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().post("/lines")
				.then().log().all().extract();
	}
}
