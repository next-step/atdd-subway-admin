package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		Map<String, String> params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");

		// when
		ExtractableResponse<Response> response = createLine(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");
		createLine(params);

		// when
		ExtractableResponse<Response> response = createLine(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");
		ExtractableResponse<Response> createResponse1 = createLine(params);

		Map<String, String> params2 = new HashMap<>();
		params2.put("color", "green lighten-1");
		params2.put("name", "2호선");
		ExtractableResponse<Response> createResponse2 = createLine(params2);

		// when
		ExtractableResponse<Response> response = findLineById(1L);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
				.map(it -> Long.parseLong(it.header("Location" .split("/")[2])))
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
		Map<String, String> params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");
		createLine(params);

		// when
		ExtractableResponse<Response> response = findLineById(1L);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		String expectedName = "신분당선";
		String resultName = response.jsonPath().getString(".name");
		assertThat(resultName).isEqualTo(expectedName);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");
		createLine(params);

		// when
		Map<String, String> params2 = new HashMap<>();
		params.put("color", "bg-blue-600");
		params.put("name", "구분당선");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(params2)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.put("/lines/1")
				.then().log().all()
				.extract();

		ExtractableResponse<Response> modifiedResponse = findLineById(1L);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		String expected = "신분당선";
		String actual = modifiedResponse.jsonPath().getString(".name");
		assertThat(actual).isEqualTo(expected);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");
		createLine(params);

		// when
		ExtractableResponse<Response> response = findLineById(1L);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> createLine(Map<String, String> params) {
		return RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/lines")
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> findLineById(Long lineId) {
		return RestAssured.given().log().all()
				.when()
				.get("/lines/" + lineId)
				.then().log().all()
				.extract();
	}
}
