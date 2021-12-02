package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	public static final String COLOR1 = "bg-red-600";
	public static final String NAME1 = "신분당선";
	public static final String COLOR2 = "bg-blue-600";
	public static final String NAME2 = "구분당선";
	public static final String LOCATION_HEADER_NAME = "Location";

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(NAME1, COLOR1);

		// then
		지하철_노선_응답_검증(response, HttpStatus.CREATED);
		지하철_노선_생성_헤더_검증(response);
		지하철_노선_생성_검증(response.header(LOCATION_HEADER_NAME));
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		지하철_노선_등록되어_있음();

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(NAME1, COLOR1);

		// then
		지하철_노선_생성_실패됨(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		지하철_노선_등록되어_있음();
		지하철_노선_등록되어_있음2();

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		지하철_노선_응답_검증(response, HttpStatus.OK);
		지하철_노선_목록_포함됨(response);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		지하철_노선_등록되어_있음();

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

		// then
		지하철_노선_응답_검증(response, HttpStatus.OK);
		지하철_노선_포함_검증(response, NAME1, COLOR1);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		지하철_노선_등록되어_있음();
		Long lineId = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineId);

		// then
		지하철_노선_응답_검증(response, HttpStatus.OK);
		지하철_노선_수정_검증(lineId);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		지하철_노선_등록되어_있음();
		Long id = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

		// then
		지하철_노선_응답_검증(response, HttpStatus.NO_CONTENT);
		지하철_노선_삭제_검증(id);
	}

	@DisplayName("지하철 노선이 추가되지 않은 상태에서 노선을 제거한다.")
	@Test
	void deleteLine2() {
		Long id = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

		// then
		지하철_노선_응답_검증(response, HttpStatus.NOT_FOUND);
	}

	private void 지하철_노선_삭제_검증(Long id) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines/" + id)
			.then().log().all()
			.extract();

		지하철_노선_응답_검증(response, HttpStatus.NOT_FOUND);
	}

	private void 지하철_노선_생성_헤더_검증(ExtractableResponse<Response> response) {
		assertThat(response.header("Location")).isNotBlank();
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("color", color);
		params.put("name", name);

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_등록되어_있음() {
		return 지하철_노선_생성_요청(NAME1, COLOR1);
	}

	private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		지하철_노선_응답_검증(response, HttpStatus.CONFLICT);
	}

	private ExtractableResponse<Response> 지하철_노선_등록되어_있음2() {
		return 지하철_노선_생성_요청(NAME2, COLOR2);
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_응답_검증(ExtractableResponse<Response> response, HttpStatus status) {
		assertThat(response.statusCode()).isEqualTo(status.value());
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(String url) {
		return RestAssured.given().log().all()
			.when()
			.get(url)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
		return 지하철_노선_조회_요청("/lines/" + id);
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id) {
		Map<String, String> params = new HashMap<>();
		params.put("color", COLOR2);
		params.put("name", NAME2);

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
		return RestAssured.given().log().all()
			.when()
			.delete("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_포함_검증(ExtractableResponse<Response> response, String name, String color) {
		assertThat(response.jsonPath().getString("name")).contains(name);
		assertThat(response.jsonPath().getString("color")).contains(color);
	}

	private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
		assertThat(response.jsonPath().getList("name")).contains(NAME1, NAME2);
		assertThat(response.jsonPath().getList("color")).contains(COLOR1, COLOR2);
	}

	private void 지하철_노선_수정_검증(Long id) {
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);
		지하철_노선_포함_검증(response, NAME2, COLOR2);
	}

	private void 지하철_노선_생성_검증(String url) {
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(url);
		지하철_노선_포함_검증(response, NAME1, COLOR1);
	}
}
