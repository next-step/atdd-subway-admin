package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.comm.CustomExtractableResponse;
import nextstep.subway.domain.Station;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

	private static final String BASIC_URL_STATIONS = "/stations";

	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
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
		ExtractableResponse<Response> Createresponse = 지하철_생성_요청("강남역");

		// then
		ExtractableResponse<Response> response = CustomExtractableResponse.get(BASIC_URL_STATIONS);
		List<String> stationNames = 지하철_리스트_이름_조회();
		
		assertAll(() -> assertThat(Createresponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
				() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertThat(stationNames).containsAnyOf("강남역"));
	}

	/**
	 * Given 지하철역을 생성하고 
	 * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 
	 * Then 지하철역 생성이 안된다
	 */
	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	@Order(2)
	void createStationWithDuplicateName() {
		// given
		지하철_생성_요청("강남역");

		// when
		ExtractableResponse<Response> response = 지하철_생성_요청("강남역");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
		지하철_생성_요청("강남역");
		지하철_생성_요청("역삼역");

		// when
		List<String> stationNames = 지하철_리스트_이름_조회();

		// then
		assertAll(() -> assertThat(stationNames).hasSize(2),
				() -> assertThat(stationNames).containsAnyOf("강남역"),
				() -> assertThat(stationNames).containsAnyOf("역삼역"));
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
		Long stationId = 지하철_생성_요청("강남역")
				.jsonPath()
					.getObject(".", Station.class)
						.getId();
		
		// when
		String url = CustomExtractableResponse.joinUrl(BASIC_URL_STATIONS, stationId);
		ExtractableResponse<Response> deleteResponse = CustomExtractableResponse.delete(url);
		List<String> stationNames = 지하철_리스트_이름_조회();

		// then
		assertAll(() -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
				() -> assertThat(stationNames).hasSize(0));
	}

	private ExtractableResponse<Response> 지하철_생성_요청(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return CustomExtractableResponse.post(BASIC_URL_STATIONS, params);
	}
	
	private List<String> 지하철_리스트_이름_조회() {
		return CustomExtractableResponse.get(BASIC_URL_STATIONS)
				.jsonPath()
					.getList("name", String.class);
	}
}
