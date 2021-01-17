package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
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
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");

		RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "신분당선");
		params1.put("color", "bg-red-600");

		ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		Map<String, String> params2 = new HashMap<>();
		params2.put("name", "1호선");
		params2.put("color", "bg-blue-600");

		ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
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
		Map<String, String> params = new HashMap<>();
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");

		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		String id = createResponse.header(HttpHeaders.LOCATION).split("/")[2];

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(URI.create("/line/" + id))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long resultId = response.jsonPath().getObject(".", LineResponse.class).getId();
		assertThat(resultId).isEqualTo(Long.parseLong(id));
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "신분당선");
		params1.put("color", "bg-red-600");

		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		String id = createResponse.header(HttpHeaders.LOCATION).split("/")[2];

		// when
		String updatedName = "1호선";
		String updatedColor = "bg-blue-600";

		Map<String, String> params2 = new HashMap<>();
		params2.put("name", updatedName);
		params2.put("color", updatedColor);

		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(params2)
			.when()
			.put(URI.create("/line/" + id))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(id));
		assertThat(lineResponse.getName()).isEqualTo(updatedName);
		assertThat(lineResponse.getColor()).isEqualTo(updatedColor);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");

		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		String id = createResponse.header(HttpHeaders.LOCATION).split("/")[2];

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(URI.create("/line/" + id))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
