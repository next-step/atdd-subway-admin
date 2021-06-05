package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

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
import nextstep.subway.line.domain.Line;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Map<String, String> parameters = new HashMap<>();
		parameters.put("color", "bg-red-600");
		parameters.put("name", "신분당선");

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(parameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> parameters = new HashMap<>();
		parameters.put("color", "bg-red-600");
		parameters.put("name", "신분당선");

		RestAssured
			.given().log().all()
			.body(parameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(parameters)
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
		Map<String, String> parameters1 = new HashMap<>();
		parameters1.put("color", "bg-red-600");
		parameters1.put("name", "신분당선");

		ExtractableResponse<Response> createResponse1 = RestAssured
			.given().log().all()
			.body(parameters1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// 지하철_노선_등록되어_있음
		Map<String, String> parameters2 = new HashMap<>();
		parameters2.put("color", "bg-green-600");
		parameters2.put("name", "2호선");

		ExtractableResponse<Response> createResponse2 = RestAssured
			.given().log().all()
			.body(parameters2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_목록_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_목록_포함됨
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());

		List<Long> resultLineIds = response.jsonPath().getList(".", Line.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());

		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> parameters = new HashMap<>();
		parameters.put("color", "bg-red-600");
		parameters.put("name", "신분당선");

		ExtractableResponse<Response> createResponse = RestAssured
			.given().log().all()
			.body(parameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_조회_요청
		String createLineId = createResponse.header("Location").split("/")[2];

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when()
			.get("/lines" + "/" + createLineId)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_동일함
		String resultLineId = response.jsonPath().get("$.id");
		assertThat(resultLineId).isEqualTo(createLineId);
	}

	@DisplayName("없는 지하철 노선을 조회한다.")
	@Test
	void getNotExistsLine() {
		// given
		// 지하철_노선_등록되어 있지 않음

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when()
			.get("/lines" + "/" + 1)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> createParameters = new HashMap<>();
		createParameters.put("color", "bg-red-600");
		createParameters.put("name", "신분당선");

		ExtractableResponse<Response> createResponse = RestAssured
			.given().log().all()
			.body(createParameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_수정_요청
		String createLineId = createResponse.header("Location").split("/")[2];

		Map<String, String> parameters = new HashMap<>();
		parameters.put("color", "bg-red-600");
		parameters.put("name", "구분당선");

		ExtractableResponse<Response> modifyResponse = RestAssured
			.given().log().all()
			.body(parameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines" + "/" + createLineId)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_수정_응답됨
		assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_수정됨
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when()
			.get("/lines" + "/" + createLineId)
			.then().log().all()
			.extract();

		String resultLineName = response.jsonPath().get("$.name");
		assertThat(resultLineName).isEqualTo("구분당선");
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> createParameters = new HashMap<>();
		createParameters.put("color", "bg-red-600");
		createParameters.put("name", "신분당선");

		ExtractableResponse<Response> createResponse = RestAssured
			.given().log().all()
			.body(createParameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		// 지하철_노선_제거_요청
		String uri = createResponse.header("Location");

		ExtractableResponse<Response> deleteResponse = RestAssured
			.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_삭제됨_응답
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// 지하철_노선_삭제됨
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when()
			.get(uri)
			.then().log().all()
			.extract();

		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
