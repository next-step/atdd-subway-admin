package nextstep.subway.station;

import static nextstep.subway.util.StationAcceptanceUtils.*;
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
import nextstep.subway.util.DatabaseCleanUpUtils;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class StationAcceptanceTest {
	@LocalServerPort
	int port;

	private final DatabaseCleanUpUtils cleanUpUtils;

	public StationAcceptanceTest(DatabaseCleanUpUtils cleanUpUtils) {
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
	 * When 지하철역을 생성하면
	 * Then 지하철역이 생성된다
	 * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
	 */
	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		// then
		ExtractableResponse<Response> allStationsResponse = 지하철역_목록_조회_요청();
		assertAll(
			() -> assertThat(allStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(allStationsResponse.jsonPath().getList("name", String.class))
				.containsAnyOf("강남역")
		);
	}

	/**
	 * Given 지하철역을 생성하고
	 * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
	 * Then 지하철역 생성이 안된다
	 */
	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성할 수 없다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		ExtractableResponse<Response> response = 지하철역_생성_요청("잠실역");

		// when
		ExtractableResponse<Response> duplicateCreationResponse = 지하철역_생성_요청("잠실역");

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(duplicateCreationResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	/**
	 * Given 2개의 지하철역을 생성하고
	 * When 지하철역 목록을 조회하면
	 * Then 2개의 지하철역을 응답 받는다
	 */
	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		// given
		지하철역_생성_요청("강남역");
		지하철역_생성_요청("역삼역");

		// when
		ExtractableResponse<Response> inquiryStationsResponse = 지하철역_목록_조회_요청();

		// then
		JsonPath jsonPath = inquiryStationsResponse.jsonPath();
		assertAll(
			() -> assertThat(inquiryStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(jsonPath.getList("name", String.class)).containsExactly("강남역", "역삼역"),
			() -> assertThat(jsonPath.getList("id", Long.class)).hasSize(2)
		);
	}

	/**
	 * Given 지하철역을 생성하고
	 * When 그 지하철역을 삭제하면
	 * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
	 */
	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		ExtractableResponse<Response> firstStationResponse = 지하철역_생성_요청("신논현역");

		// when
		String id = firstStationResponse.jsonPath().getString("id");
		ExtractableResponse<Response> deleteStationResponse = 지하철역_삭제_요청(id);

		// then
		assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
