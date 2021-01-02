package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Feature: 지하철 노선 관련 기능
 *
 *   Scenario: 지하철 노선을 생성한다.
 *     When 지하철 노선을 생성 요청한다.
 *     Then 지하철 노선이 생성된다.
 *
 *   Scenario: 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
 *     Given 지하철 노선이 등록되어 있다
 *     When 지하철 노선을 생성 요청한다.
 *     Then 지하철 노선 생성이 실패된다.
 *
 *   Scenario: 지하철 노선 목록을 조회한다.
 *     Given 지하철 노선이 등록되어 있다
 *     When 지하철 노선 목록을 조회 요청한다.
 *     Then 지하철 노선 목록이 응답된다.
 *     AND  지하철 노선 목록이 포함되어 있다.
 *
 *   Scenario: 지하철 노선을 조회한다.
 *     Given 지하철 노선이 등록되어 있다
 *     When 지하철 노선을 조회 요청한다.
 *     Then 지하철 노선이 응답된다.
 *
 *   Scenario: 지하철 노선을 수정한다.
 *     Given 지하철 노선이 등록되어 있다
 *     When 지하철 노선을 수정 요청한다.
 *     Then 지하철 노선이 수정된다.
 *
 *   Scenario: 지하철 노선을 제거한다.
 *     Given 지하철 노선이 등록되어 있다
 *     When 지하철 노선을 제거 요청한다.
 *     Then 지하철 노선 삭제된다.
 *
 */
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when : 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

		// then : 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given : 지하철_노선_등록되어_있음
		Long id = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

		// then : 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given : 지하철_노선_등록되어_있음
		List<Long> 등록된역_ID_목록 = Stream.of(
			지하철노선_생성_요청("2호선", "green"),
			지하철노선_생성_요청("5호선", "purple")
		).map(this::location_header에서_ID_추출)
			.collect(Collectors.toList());

		// when : 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();

		// then : 지하철_노선_목록_응답됨 & 지하철_노선_목록_포함됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("id", Long.class)).containsAll(등록된역_ID_목록)
		);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선_ID);

		// then : 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_수정_요청
		Map<String, String> params = new HashMap<>();
		params.put("name", "구분당선");
		params.put("color", "bg-blue-600");
		RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/" + 이호선_ID)
			.then().log().all().extract();

		// then : 지하철_노선_수정됨
		assertThat(지하철_노선_조회_요청(이호선_ID).jsonPath().<String>get("name")).isEqualTo("구분당선");
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_제거_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete("/lines/" + 이호선_ID)
			.then().log().all().extract();

		// then : 지하철_노선_삭제됨
		assertThat(지하철_노선_조회_요청(이호선_ID).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
		return RestAssured
			.given().log().all()
			.when().get("/lines/" + id)
			.then().log().all().extract();
	}

	private long location_header에서_ID_추출(ExtractableResponse<Response> extractableResponse) {
		return Long.parseLong(extractableResponse.header("Location").split("/")[2]);
	}

	private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}
}
