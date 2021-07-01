package nextstep.subway.line;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@BeforeEach
	void createStation() {
		지하철역_등록("강일역");
		지하철역_등록("하남풍산역");
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// 지하철_노선_생성_요청
		지하철_노선_등록("미사역", "보라", 1L, 2L, 10);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록("미사역", "보라", 1L, 2L, 10);

		// when
		// 기존에_존재하는_지하철_노선_생성_요청
		Map<String, String> newParams = new HashMap<>();
		newParams.put("name", "미사역");
		newParams.put("color", "보라");

		// when
		ExtractableResponse<Response> newResponse = RestAssured
			.given().log().all()
			.body(newParams)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();

		// then
		// 지하철_노선_생성_실패됨
		Assertions.assertThat(newResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록("미사역", "보라", 1L, 2L, 10);
		// 지하철_노선_등록되어_있음
		지하철_노선_등록("강일역", "보라", 1L, 2L, 10);

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();

		// then
		// 지하철_노선_목록_응답됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// then
		// 지하철_노선_목록_포함됨
		List<String> lineNames = response.jsonPath().getList(".", LineResponse.class)
			.stream().map(it -> it.getName())
			.collect(Collectors.toList());
		Assertions.assertThat(lineNames).containsAll(Arrays.asList("미사역", "강일역"));
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록("미사역", "보라", 1L, 2L, 10);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = RestAssured
		        .given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		        .when().get("/lines/{id}",1L)
		        .then().log().all().extract();

		// then
		// 지하철_노선_응답됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		Assertions.assertThat(lineResponse.getName()).isEqualTo("미사역");
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록("미사역", "보라", 1L, 2L, 10);

		// when
		// 지하철_노선_수정_요청
		Map<String, String> params = new HashMap<>();
		params.put("color", "파랑");

		ExtractableResponse<Response> response = RestAssured
		        .given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		        .when().put("/lines/{id}", 1L)
		        .then().log().all().extract();
		
		// then
		// 지하철_노선_수정됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록("미사역", "보라", 1L ,2L, 10);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete("/lines/{id}", 1L)
			.then().log().all().extract();

		// then
		// 지하철_노선_삭제됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철_노선_등록(String name, String color, Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();

		// then
		// 지하철_노선_생성됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	private void 지하철역_등록(String name){
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/stations")
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	public static ExtractableResponse<Response> 지하철_노선_등록되어있음(String name, String color, Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));

		// when
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}
}
