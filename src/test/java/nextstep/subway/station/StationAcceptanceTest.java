package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

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

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// given
		Map<String, String> params = getParameter("강남역");

		// when
		ExtractableResponse<Response> response = createStations(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		Map<String, String> params = getParameter("강남역");
		createStations(params);

		// when
		ExtractableResponse<Response> response = createStations(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		/// given
		ExtractableResponse<Response> createResponse1 = createStations(getParameter("강남역"));
		ExtractableResponse<Response> createResponse2 = createStations(getParameter("역삼역"));

		// when
		ExtractableResponse<Response> response = findAllStations();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
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
		Map<String, String> params = getParameter("강남역");
		ExtractableResponse<Response> createResponse = createStations(params);

		// when
		ExtractableResponse<Response> response = deleteStation(createResponse);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> deleteStation(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		
		return RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();
	}

	private Map<String, String> getParameter(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return params;
	}

	private ExtractableResponse<Response> createStations(Map<String, String> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> findAllStations() {
		return RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();
	}
}
