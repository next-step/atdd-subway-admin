package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.ScenarioTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LineAcceptanceTest extends ScenarioTest {
	/**
	 * Note :
	 * 이 지하철 노선 기능 테스트는
	 * 시나리오(순서)에 따라 동작함.
	* */

	private final String LINE_URL = "/lines";

	private final String LINE_GET_URL = "/lines/1";

	Map<String, String> params = new HashMap<>();

	@BeforeAll
	public void setUp() {
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	@Order(1)
	void createLine() {
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = createLine(params);

		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	private ExtractableResponse<Response> createLine(Map<String, String> params) {
		ExtractableResponse<Response> response = RestAssured.given().log().all().
				body(params).
				contentType(MediaType.APPLICATION_JSON_VALUE).
				when().
				post(LINE_URL).
				then().
				log().all().
				extract();

		return response;
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	@Order(2)
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 기존에 존재하는 지하철 노선 이름으로 지하철_노선_생성_요청
		ExtractableResponse<Response> response = createLine(params);

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	@Order(3)
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		// 지하철_노선_등록되어_있음
		params = new HashMap();
		params.put("name", "2호선");
		params.put("color", "bg-green-600");
		ExtractableResponse<Response> response = createLine(params);

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> lineListResponse = getLine(LINE_URL);

		// then
		// 지하철_노선_목록_응답됨
		assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		List<Long> resultLineIds = lineListResponse.jsonPath().getList(".", LineResponse.class).stream()
				.map(it -> it.getId())
				.collect(Collectors.toList());
		assertThat(resultLineIds.size()).isNotEqualTo(0);
	}

	private ExtractableResponse<Response> getLine(String getUrl) {
		ExtractableResponse<Response> lineListResponse = RestAssured
				.given().log().all()
				.when().get(getUrl)
				.then().log().all().extract();
		return lineListResponse;
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	@Order(4)
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> lineListResponse = getLine(LINE_GET_URL);
		// then
		// 지하철_노선_응답됨
		assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = lineListResponse.jsonPath().getObject(".",  LineResponse.class);
		assertThat(lineResponse.getId()).isNotNull();
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	@Order(5)
	void updateLine()  {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_수정_요청
		params.put("id", "1");
		params.put("name", "new_신분당선");
		ExtractableResponse<Response> patchResponse = RestAssured.given().log().all().
				body(params).
				contentType(MediaType.APPLICATION_JSON_VALUE).
				when().
				patch(LINE_URL).
				then().
				log().all().
				extract();
		assertThat(patchResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// then
		// 지하철_노선_수정됨
		ExtractableResponse<Response> lineListResponse = getLine(LINE_GET_URL);
		assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = lineListResponse.jsonPath().getObject(".",  LineResponse.class);
		assertThat(lineResponse.getName().equalsIgnoreCase(params.get("name"))).isTrue();
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	@Order(6)
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> deleteLineResponse = RestAssured.given().log().all().
				body(params).
				contentType(MediaType.APPLICATION_JSON_VALUE).
				when().
				delete(LINE_GET_URL).
				then().
				log().all().
				extract();
		assertThat(deleteLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// then
		// 지하철_노선_삭제됨
		ExtractableResponse<Response> lineListResponse = getLine(LINE_GET_URL);
		assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}
