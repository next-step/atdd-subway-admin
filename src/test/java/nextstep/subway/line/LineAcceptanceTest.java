package nextstep.subway.line;

import static nextstep.subway.util.LineAcceptanceUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.line.LineResponse;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
	}

	/**
	 *  When 지하철 노선을 생성하면</br>
	 *  Then 지하철 노선이 생성된다</br>
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

}
