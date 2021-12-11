package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
	public static final Station 삼성역 = new Station("삼성역");
	public static final Station 역삼역 = new Station("역삼역");
	public static final Station 선릉역 = new Station("선릉역");
	public static final Station 강남역 = new Station("강남역");

	@Autowired
	private static StationRepository stationRepository;

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", "강남역");

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", "강남역");
		RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then()
			.log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		/// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "강남역");
		ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		Map<String, String> params2 = new HashMap<>();
		params2.put("name", "역삼역");
		ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();

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
		Map<String, String> params = new HashMap<>();
		params.put("name", "강남역");
		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		// when
		String uri = createResponse.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	public static Station 지하철역_생성되어_있음_삼성역() {
		StationResponse stationResponse = 지하철역_생성_요청(삼성역.getName()).as(StationResponse.class);
		return Station.from(stationResponse);
	}

	public static Station 지하철역_생성되어_있음_역삼역() {
		StationResponse stationResponse = 지하철역_생성_요청(역삼역.getName()).as(StationResponse.class);
		return Station.from(stationResponse);
	}

	public static Station 지하철역_생성되어_있음_선릉역() {
		StationResponse stationResponse = 지하철역_생성_요청(선릉역.getName()).as(StationResponse.class);
		return Station.from(stationResponse);
	}

	public static Station 지하철역_생성되어_있음_강남역() {
		StationResponse stationResponse = 지하철역_생성_요청(강남역.getName()).as(StationResponse.class);
		return Station.from(stationResponse);
	}

	public static void 지하철역_노선에_포함_검증(ExtractableResponse<Response> response) {
		assertThat(response.as(LineResponse.class).getStations().size()).isGreaterThan(0);
	}

	public static void 지하철역_노선_목록에_포함_검증(ExtractableResponse<Response> response) {
		response.jsonPath().getList("stations").forEach(stations -> {
			assertThat(((List<Object>)stations).size()).isGreaterThan(0);
		});
	}

	private static ExtractableResponse<Response> 지하철역_생성_요청(String stationName) {
		Map<String, String> params = new HashMap<>();
		params.put("name", stationName);

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}
}
