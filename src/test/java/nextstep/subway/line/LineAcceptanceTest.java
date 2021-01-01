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
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Map<String, String> params = generateParam("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

		// then
		assertAll(
			() -> assertThat(response).isNotNull(),
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.as(Line.class).getId()).isNotNull()
		);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(generateParam("2호선", "green"));

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선전체를 조회한다.")
	@Test
	void getLines() {
		// given
		지하철_노선_생성("1호선", "blue");
		지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_전체_조회_요청();
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
		Line line = 지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.as(Line.class)).isNotNull(),
			() -> assertThat(response.as(Line.class).getId()).isNotNull()
		);
	}

	@DisplayName("지하철 없는 노선 조회한다.")
	@Test
	void getLineFailTest() {
		// given
		Line line = 지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId() + 1);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		Line line = 지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(line.getId(), generateParam("3호선", "orange"));

		// then
		assertAll(
			() -> assertThat(response).isNotNull(),
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.as(Line.class)).isNotEqualTo(line),
			() -> assertThat(response.as(Line.class).getId()).isEqualTo(line.getId()),
			() -> assertThat(response.as(Line.class).getName()).isEqualTo("3호선")
		);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		Line line = 지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노삭_삭제_요청(line.getId());

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
			() -> assertThat(지하철_노선_조회_요청(line.getId()).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("지하철 잘못된 노선을 제거한다.")
	@Test
	void deleteFailLine() {
		// given
		Line line = 지하철_노선_생성("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철_노삭_삭제_요청(line.getId() + 1);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	private Line 지하철_노선_생성(String name, String color) {
		return 지하철_노선_생성_요청(generateParam(name, color)).as(Line.class);
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}

	private ExtractableResponse<Response> 지하철_노선_전체_조회_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines")
			.then().log().all().extract();
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines/" + id)
			.then().log().all().extract();
		return response;
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Map<String, String> params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노삭_삭제_요청(Long id) {
		return RestAssured
			.given().log().all()
			.when().delete("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private Map<String, String> generateParam(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		return params;
	}
}
