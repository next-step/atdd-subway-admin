package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<String, String> params = 라인_파람_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성(params);

		// then
		assertAll(
			() -> assertThat(response).isNotNull(),
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
		);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		Map<String, String> params = 라인_파람_생성("2호선", "green");
		지하철_노선_생성(params);

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		Map<String, String> line1Param = 라인_파람_생성("1호선", "blue");
		Map<String, String> line2Param = 라인_파람_생성("2호선", "green");
		지하철_노선_생성(line1Param);
		지하철_노선_생성(line2Param);

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회();
		List<LineResponse> lines = response.jsonPath().getList(".", LineResponse.class);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(lines).hasSize(2)
		);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_조회_요청

		// then
		// 지하철_노선_응답됨
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_수정_요청

		// then
		// 지하철_노선_수정됨
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

	private ExtractableResponse<Response> 지하철_노선_생성(Map<String, String> params) {
		// when
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}

	private ExtractableResponse<Response> 지하철_노선_조회() {
		return RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();
	}

	private Map<String, String> 라인_파람_생성(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		return params;
	}
}
