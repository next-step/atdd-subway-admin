package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		String lineName = "신분당선";

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineName);

		// then
		// 지하철_노선_생성됨
		지하철_노선_생성됨(response, lineName);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		String line1 = 지하철_노선_등록되어_있음("신분당선");

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(line1);

		// then
		지하철_노선_생성_실패됨(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다")
	@Test
	void getLines() {
		// given
		List<String> givenLines = 지하철_노선들_등록되어_있음("신분당선", "2호선");

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		assertAll(
			() -> 지하철_노선_응답됨(response),
			() -> 지하철_노선_목록_포함됨(response, givenLines)
		);

	}

	@DisplayName("지하철 노선을 조회한다")
	@Test
	void getLine() {
		// given
		지하철_노선_등록되어_있음("신분당선");

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청();

		// then
		지하철_노선_응답됨(response);
	}

	@DisplayName("지하철 노선을 수정한다")
	@Test
	void updateLine() {
		// given
		String lineName = 지하철_노선_등록되어_있음("신분당선") + "-updated";

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineName);

		// then
		지하철_노선_수정됨(response, lineName);
	}

	@DisplayName("지하철 노선을 제거한다")
	@Test
	void deleteLine() {
		// given
		지하철_노선_등록되어_있음("신분당선");

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청();

		// then
		지하철_노선_삭제됨(response);
	}

	private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 지하철_노선_제거_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/1")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_생성됨(ExtractableResponse<Response> response, String lineName) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.header("Location")).isNotBlank(),
			() -> assertThat(response.body().jsonPath().getLong("id")).isPositive(),
			() -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선"),
			() -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600"),
			() -> assertThat(response.body().jsonPath().getString("createdDate")).isNotNull(),
			() -> assertThat(response.body().jsonPath().getString("modifiedDate")).isNotNull()
		);
	}

	private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private String 지하철_노선_등록되어_있음(String lineName) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", lineName);
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
		return lineName;
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/1")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(String lineName) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", lineName);
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private List<String> 지하철_노선들_등록되어_있음(String... lines) {
		return Arrays.stream(lines).map(this::지하철_노선_등록되어_있음).collect(Collectors.toList());
	}

	private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<String> givenLines) {
		List<String> expectedLineIds = givenLines;
		List<String> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
			.stream()
			.map(LineResponse::getName)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철_노선_수정됨(ExtractableResponse<Response> response, String lineName) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.body().jsonPath().getString("name"))
				.isEqualTo(lineName)
		);
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(String lineName) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", lineName);

		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(params)
			.when()
			.put("/lines/1")
			.then().log().all()
			.extract();
	}

}
