package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	private static final LineRequest lineNumber1 = new LineRequest("1호선", "Blue");
	private static final LineRequest lineNumber2 = new LineRequest("2호선", "Green");

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		//when
		ExtractableResponse<Response> response = 지하철_노선을_생성한다(lineNumber2);
		// then
		지하철_노선_생성됨(response, lineNumber2);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		지하철_노선_생성되어_있음(lineNumber2);
		// when
		ExtractableResponse<Response> response = 지하철_노선을_생성한다(lineNumber2);
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> createResponse1 = 지하철_노선_생성되어_있음(lineNumber1);
		ExtractableResponse<Response> createResponse2 = 지하철_노선_생성되어_있음(lineNumber2);
		// when
		ExtractableResponse<Response> response = 모든_지하철_노선을_조회한다();
		// then
		지하철_노선_목록_응답됨(response);
		지하철_노선_목록_포함됨(response, Arrays.asList(createResponse1, createResponse2));

	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> createResponse = 지하철_노선_생성되어_있음(lineNumber2);
		// when
		Long id = createResponse.jsonPath().getLong("id");
		ExtractableResponse<Response> response = 단일_지하철_노선을_조회한다(id);
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = 지하철_노선_생성되어_있음(lineNumber2);
		// when
		long id = createResponse.jsonPath().getLong("id");
		ExtractableResponse<Response> response = 지하철_노선을_수정한다(lineNumber1, id);
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> createResponse = 지하철_노선_생성되어_있음(lineNumber1);
		// when
		long id = createResponse.jsonPath().getLong("id");
		ExtractableResponse<Response> response = 지하철_노선을_제거한다(id);
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	ExtractableResponse<Response> 지하철_노선을_생성한다(LineRequest lineRequest) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
		return response;
	}

	void 지하철_노선_생성됨(ExtractableResponse<Response> response, LineRequest lineRequest) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
		assertThat(response.body().jsonPath().getString("name")).isEqualTo(lineRequest.getName());
		assertThat(response.body().jsonPath().getString("color")).isEqualTo(lineRequest.getColor());
	}

	ExtractableResponse<Response> 지하철_노선_생성되어_있음(LineRequest lineRequest) {
		ExtractableResponse<Response> response = 지하철_노선을_생성한다(lineRequest);
		지하철_노선_생성됨(response, lineRequest);
		return response;
	}

	void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createResponses) {
		List<Long> expectedLineIds = createResponses.stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	ExtractableResponse<Response> 모든_지하철_노선을_조회한다() {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
		return response;
	}

	ExtractableResponse<Response> 단일_지하철_노선을_조회한다(Long id) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines/" + id)
			.then().log().all()
			.extract();
		return response;
	}

	ExtractableResponse<Response> 지하철_노선을_수정한다(LineRequest lineRequest, Long id) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + id)
			.then().log().all()
			.extract();
		return response;
	}

	ExtractableResponse<Response> 지하철_노선을_제거한다(Long id) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + id)
			.then().log().all()
			.extract();
		return response;
	}
}
