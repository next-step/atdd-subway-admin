package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		// 지하철_노선_생성_요청
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> response = 지하철노선을_생성_요청(params);
		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		지하철노선을_생성_요청(params);
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선을_생성_요청(params);
		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("color", "bg-red-600");
		params1.put("name", "신분당선");

		Map<String, String> params2 = new HashMap<>();
		params2.put("color", "bg-yellow-600");
		params2.put("name", "분당선");

		ExtractableResponse<Response> createResponse1 = 지하철노선을_생성_요청(params1);
		ExtractableResponse<Response> createResponse2 = 지하철노선을_생성_요청(params2);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
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

		ExtractableResponse<Response> createResponse = 지하철노선을_생성_요청(params);

		LineResponse addLineResponse = createResponse.response().getBody().as(LineResponse.class);
		Long id = addLineResponse.getId();

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines/" + id)
			.then().log().all()
			.extract();
		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
		assertThat(lineResponse.getId()).isEqualTo(id);
	}
	@DisplayName("존재하지 않는 노선 업데이트요청시 오류발생확인")
	@Test
	void test_updateLine_존재하지않음(){
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> errorResponse = 지하철노선을_수정요청(999L, params);
		assertThat(errorResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");

		ExtractableResponse<Response> addLineResponse = 지하철노선을_생성_요청(params);
		LineResponse createdLine = addLineResponse.response().getBody().as(LineResponse.class);

		// when
		// 지하철_노선_수정_요청
		Map<String, String> updateParams = new HashMap<>();
		String updateColor = "bg-yellow-600";
		String updateName = "분당선";
		updateParams.put("color", updateColor);
		updateParams.put("name", updateName);
		ExtractableResponse<Response> lineResponse = 지하철노선을_수정요청(createdLine.getId(), updateParams);

		// then
		// 지하철_노선_수정됨
		assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> addLineResponse = this.지하철노선을_생성_요청(params);
		LineResponse createdLine = addLineResponse.response().as(LineResponse.class);
		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> deleteResponse = 지하철노선을_삭제요청(createdLine.getId());
		// then
		// 지하철_노선_삭제됨
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	@DisplayName("없는 아이디를 제거하면 오류발생 확인")
	void test_delete없는아이디(){
		ExtractableResponse<Response> errorResponse = 지하철노선을_삭제요청(999L);
		assertThat(errorResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철노선을_삭제요청(long deleteID) {
		ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
			.when()
			.delete("/lines/" + deleteID)
			.then()
			.log().all().extract();
		return deleteResponse;
	}

	private ExtractableResponse<Response> 지하철노선을_생성_요청(Map<String, String> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철노선을_수정요청(long createdLineID,
		Map<String, String> updateParams) {
		return RestAssured.given().log().all()
			.body(updateParams)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + createdLineID)
			.then().log().all().extract();
	}
}
