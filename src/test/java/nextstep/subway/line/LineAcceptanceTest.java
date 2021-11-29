package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given, when
		long upStationId = 1;
		long downStationId = 2;
		long distance = 10;
		ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", upStationId, downStationId, distance);

		// then
		지하철_노선_생성됨(response);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		long upStationId = 1;
		long downStationId = 2;
		long distance = 10;
		지하철_노선_등록되어_있음("신분당선", upStationId, downStationId, distance);

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", upStationId, downStationId, distance);

		// then
		지하철_노선_생성_실패됨(response);
	}

	private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String lineName, long upStationId, long downStationId,
		long distance) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", lineName);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	@DisplayName("지하철 노선 목록을 조회한다")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> response1 = 지하철_노선_등록되어_있음("신분당선", 1, 2, 10);
		ExtractableResponse<Response> response2 = 지하철_노선_등록되어_있음("2호선", 3, 4, 9);

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		assertAll(
			() -> 지하철_노선_응답됨(response),
			() -> 지하철_노선_목록_포함됨(response, Arrays.asList(response1, response2)),
			() -> 지하철_노선_역_목록_포함됨(response, Arrays.asList(response1, response2))
		);

	}

	private void 지하철_노선_역_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> givens) {

	}

	@DisplayName("지하철 노선을 조회한다")
	@Test
	void getLine() {
		// given
		지하철_노선_등록되어_있음("신분당선", 1, 2, 10);

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청();

		// then
		지하철_노선_응답됨(response);
	}

	@DisplayName("지하철 노선을 수정한다")
	@Test
	void updateLine() {
		// given
		String lineName = "신분당선";
		지하철_노선_등록되어_있음(lineName, 1, 2, 10);
		String updatedLineName = lineName + "-updated";

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(updatedLineName);

		// then
		지하철_노선_수정됨(response, updatedLineName);
	}

	@DisplayName("지하철 노선을 제거한다")
	@Test
	void deleteLine() {
		// given
		지하철_노선_등록되어_있음("신분당선", 1, 2, 10);

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청();

		// then
		지하철_노선_삭제됨(response);
	}

	private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 지하철_노선_제거_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/1")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.header("Location")).isNotBlank(),
			() -> assertThat(response.body().jsonPath().getLong("id")).isPositive(),
			() -> assertThat(response.body().jsonPath().getString("name")).isNotNull(),
			() -> assertThat(response.body().jsonPath().getString("color")).isNotNull(),
			() -> assertThat(response.body().jsonPath().getString("createdDate")).isNotNull(),
			() -> assertThat(response.body().jsonPath().getString("modifiedDate")).isNotNull(),
			() -> assertThat(response.body().jsonPath().getString("stations[0].id")).isNotNull(),
			() -> assertThat(response.body().jsonPath().getString("stations[1].id")).isNotNull()
		);
	}

	private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/1")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(String lineName, long upStationId, long downStationId,
		long distance) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", lineName);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> givens) {
		List<Long> expectedLineIds = givens.stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class)
			.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철_노선_수정됨(ExtractableResponse<Response> response, String updatedLineName) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.body().jsonPath().getString("name"))
				.isEqualTo(updatedLineName)
		);
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(String lineName) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "bg-red-600");
		params.put("name", lineName);

		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(params)
			.when()
			.put("/lines/1")
			.then().log().all()
			.extract();
	}

}
