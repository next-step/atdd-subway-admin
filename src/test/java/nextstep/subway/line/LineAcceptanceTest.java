package nextstep.subway.line;

import static nextstep.subway.util.LineAcceptanceUtils.*;
import static nextstep.subway.util.ResponseExtractUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestConstructor;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.LineUpdateRequest;
import nextstep.subway.util.DatabaseCleanUpUtils;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class LineAcceptanceTest {
	@LocalServerPort
	int port;

	private final DatabaseCleanUpUtils cleanUpUtils;

	public LineAcceptanceTest(DatabaseCleanUpUtils cleanUpUtils) {
		this.cleanUpUtils = cleanUpUtils;
	}

	@BeforeEach
	public void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
			cleanUpUtils.afterPropertiesSet();
		}
		cleanUpUtils.cleanUp();
	}

	/**
	 * When 지하철 노선을 생성하면
	 * Then 지하철 노선이 생성된다
	 * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
	 */
	@DisplayName("노선을 생성한다.")
	@Test
	void createLineTest() {
		// when
		final String name = "신분당선";
		final String upStationName = "강남역";
		final String downStationName = "역삼역";
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, "bg-red-600", upStationName, downStationName);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		// then
		ExtractableResponse<Response> allLinesResponse = 지하철_노선_목록_조회_요청();
		JsonPath createLineResponseBody = response.jsonPath();
		JsonPath allLinesResponseBody = allLinesResponse.jsonPath();
		assertAll(
			() -> assertThat(createLineResponseBody.getList("stations", LineResponse.class)).hasSize(2),
			() -> assertThat(createLineResponseBody.getList("stations.name"))
				.containsAnyOf(upStationName, downStationName),
			() -> assertThat(allLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(allLinesResponseBody.getList("name")).contains(name)
		);
	}

	/**
	 * Given 2 개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
	 */
	@DisplayName("노선 목록을 조회한다.")
	@Test
	void getLinesTest() {
		// given
		지하철_노선_생성_요청("신분당선", "bg-red-600", "강남역", "양재역");
		지하철_노선_생성_요청("2호선", "bg-green-600", "역삼역", "삼성역");

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		JsonPath responseBody = response.jsonPath();
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseBody.getList("name")).hasSize(2);
		assertThat(responseBody.getList("name")).containsAnyOf("신분당선", "2호선");
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
	 */
	@Test
	@DisplayName("노선을 조회한다.")
	void getLineTest() {
		// given
		ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "bg-red-600", "강남역", "양재역");
		Long id = id(createResponse);

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);

		// then
		JsonPath jsonPath = response.jsonPath();
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() ->assertThat(jsonPath.getString("name")).isEqualTo("신분당선"),
			() -> assertThat(jsonPath.getList("stations.name"))
				.containsAnyOf("강남역", "양재역")
		);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@Test
	@DisplayName("노선을 수정한다.")
	void updateLineTest() {
		// given
		ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "bg-red-600", "강남역", "양재역");
		Long id = id(createResponse);
		LineUpdateRequest request = LineUpdateRequest.of("다른분당선", "bg-red-500");

		// when
		ExtractableResponse<Response> updateResponse = 지하철_노선_수정_요청(id, request);
		ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(id);

		// then
		JsonPath responseBody = getResponse.jsonPath();
		assertAll(
			() -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(responseBody.getString("name")).isEqualTo("다른분당선"),
			() -> assertThat(responseBody.getString("color")).isEqualTo("bg-red-500"),
			() -> assertThat(responseBody.getList("stations.name"))
				.containsAnyOf("강남역", "양재역")
		);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@Test
	@DisplayName("노선을 삭제한다.")
	void deleteLineTest() {
		// given
		ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "bg-red-600", "강남역", "양재역");
		Long id = id(createResponse);

		// when
		ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제_요청(id);
		ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(id);

		System.out.println("getResponse = " + getResponse);
		JsonPath jsonPath = getResponse.jsonPath();
		// then
		assertAll(
			() -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
			() -> assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
		);
	}

}
