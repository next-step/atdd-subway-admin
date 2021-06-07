package nextstep.subway.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

public class LineAcceptanceFixture {

	public static final LineRequest 신분당선 = new LineRequest("신분당선", "red darken-1");
	public static final LineRequest 분당선 = new LineRequest("분당선", "yellow darken-1");

	public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
		return RestAssured
			.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all()
			.extract();
	}

	public static void 지하철_노선_생성됨(LineRequest lineRequest, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
		assertThat(response.header("Location")).isNotBlank();
		assertThat(response.jsonPath().getLong("id")).isNotNull();
		assertThat(response.jsonPath().getString("name")).isEqualTo(lineRequest.getName());
		assertThat(response.jsonPath().getString("color")).isEqualTo(lineRequest.getColor());
	}

	public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest lineRequest) {
		return 지하철_노선_생성_요청(lineRequest);
	}

	public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return RestAssured
			.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines")
			.then().log().all().extract();
	}

	public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
	}

	public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
		ExtractableResponse<Response>... createResponses) {
		List<Long> expectedLineIds = Arrays.stream(createResponses)
			.map(res -> res.jsonPath().getLong("id"))
			.collect(toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(res -> res.getId())
			.collect(toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines/{id}", createResponse.jsonPath().getLong("id"))
			.then().log().all().extract();
		return response;
	}

	public static void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineRequest lineRequest) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
		assertThat(response.jsonPath().getLong("id")).isNotNull();
		assertThat(response.jsonPath().getString("name")).isEqualTo(lineRequest.getName());
		assertThat(response.jsonPath().getString("color")).isEqualTo(lineRequest.getColor());
	}

	public static ExtractableResponse<Response> 지하철_노선_수정_요청(
		ExtractableResponse<Response> createResponse, LineRequest lineRequest) {
		return RestAssured
			.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/{id}", createResponse.jsonPath().getLong("id"))
			.then().log().all().extract();
	}

	public static void 지하철_노선_수정됨(ExtractableResponse<Response> updateResponse,
		ExtractableResponse<Response> changedResponse, LineRequest lineRequest) {
		assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		지하철_노선_응답됨(changedResponse, lineRequest);
	}

	public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createResponse) {
		return RestAssured
			.given().log().all()
			.when().delete("/lines/{id}", createResponse.jsonPath().getLong("id"))
			.then().log().all().extract();
	}

	public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response,
		ExtractableResponse<Response> notFoundResponse) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(notFoundResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}
}
