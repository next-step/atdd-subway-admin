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
		// when
		ExtractableResponse<Response> response = 노선_생성_요청("신분당선", "bg-red-600");

		// then
		노선_생성_성공(response);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		노선_생성_요청("신분당선", "bg-red-600");

		// when
		ExtractableResponse<Response> response = 노선_생성_요청("신분당선", "bg-red-600");

		// then
		노선_생성_실패(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> createResponse1 = 노선_생성_요청("신분당선", "bg-red-600");
		ExtractableResponse<Response> createResponse2 = 노선_생성_요청("1호선", "bg-blue-600");

		// when
		ExtractableResponse<Response> response = 노선_목록_조회_요청();

		// then
		노선_목록_조회_성공(createResponse1, createResponse2, response);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선_생성_요청("신분당선", "bg-red-600");
		String id = getIdByLocation(createResponse);

		// when
		ExtractableResponse<Response> response = 노선_조회_요청(id);

		// then
		노선_조회_성공(id, response);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선_생성_요청("신분당선", "bg-red-600");
		String id = getIdByLocation(createResponse);

		// when
		String updatedName = "1호선";
		String updatedColor = "bg-blue-600";

		ExtractableResponse<Response> response = 노선_수정_요청(id, updatedName, updatedColor);

		// then
		노선_수정_성공(id, updatedName, updatedColor, response);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선_생성_요청("신분당선", "bg-red-600");
		String id = getIdByLocation(createResponse);

		// when
		ExtractableResponse<Response> response = 노선_제거_요청(id);

		// then
		노선_제거_성공(response);
	}

	private String getIdByLocation(ExtractableResponse<Response> createResponse) {
		return createResponse.header(HttpHeaders.LOCATION).split("/")[2];
	}

	private ExtractableResponse<Response> 노선_생성_요청(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private void 노선_생성_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	private void 노선_생성_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 노선_목록_조회_요청() {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private void 노선_목록_조회_성공(ExtractableResponse<Response> createResponse1,
		ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
			.map(it -> Long.parseLong(getIdByLocation(it)))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	private ExtractableResponse<Response> 노선_조회_요청(String id) {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(URI.create("/line/" + id))
			.then().log().all()
			.extract();
	}

	private void 노선_조회_성공(String id, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long resultId = response.jsonPath().getObject(".", LineResponse.class).getId();
		assertThat(resultId).isEqualTo(Long.parseLong(id));
	}

	private ExtractableResponse<Response> 노선_수정_요청(String id, String updatedName, String updatedColor) {
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
		return response;
	}

	private void 노선_수정_성공(String id, String updatedName, String updatedColor, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(id));
		assertThat(lineResponse.getName()).isEqualTo(updatedName);
		assertThat(lineResponse.getColor()).isEqualTo(updatedColor);
	}

	private ExtractableResponse<Response> 노선_제거_요청(String id) {
		return RestAssured.given().log().all()
			.when()
			.delete(URI.create("/line/" + id))
			.then().log().all()
			.extract();
	}

	private void 노선_제거_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
