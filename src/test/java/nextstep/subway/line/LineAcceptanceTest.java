package nextstep.subway.line;

import static nextstep.subway.factory.StationFactory.*;
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
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	private ExtractableResponse<Response> 라인_2호선_생성() {
		// given
		// 지하철 역
		final ExtractableResponse<Response> upStation = 지하철역_생성("강남역");
		final ExtractableResponse<Response> downStation = 지하철역_생성("역삼역");

		// and
		// 지하철_노선
		Map<String, Object> params = new HashMap<>();
		params.put("name", "2호선");
		params.put("color", "green");
		params.put("upStationId", upStation.jsonPath().getLong("id"));
		params.put("downStationId", downStation.jsonPath().getLong("id"));
		params.put("distance", 10);

		// when
		// 지하철_노선_생성_요청
		final ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		return response;
	}

	private ExtractableResponse<Response> 라인_3호선_생성() {
		// given
		// 지하철 역
		final ExtractableResponse<Response> upStation = 지하철역_생성("양재역");
		final ExtractableResponse<Response> downStation = 지하철역_생성("매봉역");

		// and
		// 지하철_노선
		Map<String, Object> params = new HashMap<>();
		params.put("name", "3호선");
		params.put("color", "orange");
		params.put("upStationId", upStation.jsonPath().getLong("id"));
		params.put("downStationId", downStation.jsonPath().getLong("id"));
		params.put("distance", 10);

		// when
		// 지하철_노선_생성_요청
		final ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		return response;
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		final ExtractableResponse<Response> response = 라인_2호선_생성();

		// then
		// 지하철_노선_생성됨
		지하철_노선_생성됨(response);
	}

	private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
		assertThat(response.jsonPath().getObject(".", LineResponse.class).getStations()).isNotEmpty();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		라인_2호선_생성();

		// when
		// 지하철_노선_생성_요청
		final ExtractableResponse<Response> response = 라인_2호선_생성();

		// then
		// 지하철_노선_생성_실패됨
		지하철_노선_생성_실패됨(response);
	}

	private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		final ExtractableResponse<Response> createResponse1 = 라인_2호선_생성();
		final ExtractableResponse<Response> createResponse2 = 라인_3호선_생성();

		// when
		// 지하철_노선_목록_조회_요청
		final ExtractableResponse<Response> response = 지하철_노선_목록_조회();

		// then
		// 지하철_노선_목록_응답됨
		// 지하철_노선_목록_포함됨
		지하철_노선_목록_확인(createResponse1, createResponse2, response);
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회() {
		return RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_목록_확인(ExtractableResponse<Response> createResponse1,
		ExtractableResponse<Response> createResponse2,
		ExtractableResponse<Response> response) {
		지하철_노선_응답됨(response);
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		final ExtractableResponse<Response> createResponse = 라인_2호선_생성();

		// when
		// 지하철_노선_조회_요청
		final ExtractableResponse<Response> response = 지하철_노선_조회(
			createResponse);

		// then
		// 지하철_노선_응답됨
		지하철_노선_응답됨(response);
	}

	private ExtractableResponse<Response> 지하철_노선_조회(ExtractableResponse<Response> createResponse) {
		final ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get(createResponse.header("Location"))
			.then().log().all()
			.extract();
		return response;
	}

	private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		final ExtractableResponse<Response> createResponse = 라인_2호선_생성();

		Map<String, String> params = new HashMap<>();
		params.put("name", "3호선");
		params.put("color", "green");

		// when
		// 지하철_노선_수정_요청
		final ExtractableResponse<Response> response = 지하철_노선_수정(
			createResponse, params);

		// then
		// 지하철_노선_수정됨
		지하철_노선_응답됨(response);
		assertThat(response.jsonPath().getString("name")).isEqualTo(params.get("name"));
	}

	private ExtractableResponse<Response> 지하철_노선_수정(ExtractableResponse<Response> createResponse,
		Map<String, String> params) {
		final ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put(createResponse.header("Location"))
			.then().log().all()
			.extract();
		return response;
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		final ExtractableResponse<Response> createResponse = 라인_2호선_생성();

		// when
		// 지하철_노선_제거_요청
		String uri = createResponse.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();

		// then
		// 지하철_노선_삭제됨
		지하철_노선_삭제됨(response);
	}

	private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
