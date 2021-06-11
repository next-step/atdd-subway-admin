package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	private static final String URL_CREATE_STATION = "/stations";
	private static final String URL_GET_STATIONS = "/stations";

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		String stationsName = "강남역";
		지하철역_생성_요청_및_성공_체크(stationsName);

		// when
		ExtractableResponse<Response> response = 지하철역_생성_요청(stationsName);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		/// given
		ExtractableResponse<Response> station_강남역 = 지하철역_생성_요청("강남역");
		ExtractableResponse<Response> station_역삼역 = 지하철역_생성_요청("역삼역");

		// when
		ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Arrays.asList(station_강남역, station_역삼역).stream()
										   .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
										   .collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
										   .map(it -> it.getId())
										   .collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

		// when
		String uri = createResponse.header("Location");
		ExtractableResponse<Response> response = 지하철역_제거_요청(uri);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	public static ExtractableResponse<Response> 지하철역_생성_요청(final String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		return post(URL_CREATE_STATION, params);
	}

	public static StationResponse 지하철역_생성_요청_및_성공_체크(final String name) {
		ExtractableResponse<Response> response = 지하철역_생성_요청(name);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		return response.as(StationResponse.class);
	}

	public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
		return get(URL_GET_STATIONS);
	}

	public static ExtractableResponse<Response> 지하철역_제거_요청(final String uri) {
		return delete(uri);
	}
}
