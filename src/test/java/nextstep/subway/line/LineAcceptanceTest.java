package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600");
		// then
		지하철_노선_생성됨(response);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_생성_요청("신분당선", "bg-red-600");
		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600");
		// then
		지하철_노선_생성_실패됨(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createdLine1 = 지하철_노선_생성_요청("신분당선", "bg-red-600");
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createdLine2 = 지하철_노선_생성_요청("2호선", "bg-green-600");
		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
		// then
		지하철_노선_목록_응답됨(response);
		지하철_노선_목록_포함됨(response, createdLine1, createdLine2);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createdLine = 지하철_노선_생성_요청("신분당선", "bg-red-600");
		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLine);
		// then
		지하철_노선_응답됨(createdLine, response);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> beforeLine = 지하철_노선_생성_요청("신분당선", "bg-red-600");
		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(beforeLine, "2호선", "bg-green-600");
		// then
		지하철_노선_수정됨(beforeLine, response);
	}

	private void 지하철_노선_수정됨(ExtractableResponse<Response> beforeLine, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createdLine, String name, String color) {
		Long lineId = getLineId(createdLine);
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/{id}", lineId)
			.then().log().all()
			.extract();
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_제거_요청

		// then
		// 지하철_노선_삭제됨
	}

	private void 지하철_노선_생성됨(final ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color) {
		return RestAssured.given().log().all()
			.body(new LineRequest(name, color))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, ExtractableResponse<Response>... createdLine) {

		List<Long> savedLineIds = Arrays.stream(createdLine).
			map(line -> getLineId(line))
			.collect(Collectors.toList());

		List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
		assertThat(actualLineIds).containsAll(savedLineIds);
	}

	private Long getLineId(ExtractableResponse<Response> line) {
		return Long.parseLong(line.header("Location").split("/")[2]);
	}

	private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		// when
		return RestAssured.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();
	}


	private void 지하철_노선_응답됨(ExtractableResponse<Response> createdLine, ExtractableResponse<Response> response) {
		Long lineId = getLineId(createdLine);

		Long expectedId = response.jsonPath().getObject(".", LineResponse.class).getId();
		assertThat(lineId).isEqualTo(expectedId);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createdLine) {
		Long lineId = getLineId(createdLine);
		return RestAssured.given().log().all()
			.when().get("/lines/{id}", lineId)
			.then().log().all().extract();
	}

}
