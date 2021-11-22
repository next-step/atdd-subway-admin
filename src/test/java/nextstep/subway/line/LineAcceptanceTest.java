package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
		assertThat(response.body().jsonPath().getLong("id")).isPositive();
		assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
		assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
		assertThat(response.body().jsonPath().getString("createdDate")).isNotNull();
		assertThat(response.body().jsonPath().getString("modifiedDate")).isNotNull();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> givenResponse1 = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
		// 지하철_노선_등록되어_있음
		Map<String, String> params2 = new HashMap<>();
		params2.put("color", "bg-green-600");
		params2.put("name", "2호선");
		ExtractableResponse<Response> givenResponse2 = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_목록_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_노선_목록_포함됨
		List<Long> expectedLineIds = Stream.of(givenResponse1, givenResponse2)
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> givenResponse1 = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		long givenId = Long.parseLong(givenResponse1.header("Location")
			.split("/")[2]);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/" + givenId)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.body().jsonPath().getLong("id"))
			.isEqualTo(givenId);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> givenResponse1 = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
		long givenId = Long.parseLong(givenResponse1.header("Location")
			.split("/")[2]);

		Map<String, String> body = new HashMap<>();
		body.put("color", "bg-blue-600");
		body.put("name", "구분당선");

		// when
		// 지하철_노선_수정_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(body)
			.when()
			.put("/lines/" + givenId)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_수정됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.body().jsonPath().getString("color"))
			.isEqualTo(body.get("color"));
		assertThat(response.body().jsonPath().getString("name"))
			.isEqualTo(body.get("name"));
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> givenResponse1 = RestAssured.given()
			.log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
		long givenId = Long.parseLong(givenResponse1.header("Location")
			.split("/")[2]);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + givenId)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_삭제됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
