package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	private StationRequest gangNamStation;
	private StationRequest sungSuStation;

	@BeforeEach
	void stationSetUp() {
		gangNamStation = new StationRequest("강남역");
		sungSuStation = new StationRequest("성수역");
	}

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> response = 지하철역_생성하기(gangNamStation);
		// then
		지하철역_생성됨(response, gangNamStation);
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		지하철역_생성되어_있음(gangNamStation);
		// when
		ExtractableResponse<Response> response = 지하철역_생성하기(gangNamStation);
		// then
		지하철_역_생성_실패됨(response);
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		/// given
		ExtractableResponse<Response> createResponse1 = 지하철역_생성되어_있음(gangNamStation);
		ExtractableResponse<Response> createResponse2 = 지하철역_생성되어_있음(sungSuStation);
		// when
		ExtractableResponse<Response> response = 지하철_역_목록_조회하기();
		// then
		지하철_역_목록_응답됨(response);
		지하철_역_목록_포함됨(response, Arrays.asList(createResponse1, createResponse2));
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		ExtractableResponse<Response> createResponse = 지하철역_생성되어_있음(gangNamStation);
		// when
		Long id = createResponse.jsonPath().getLong("id");
		ExtractableResponse<Response> response = 지하철_역_삭제하기(id);
		// then
		지하철_역_삭제_확인(response);
	}

	ExtractableResponse<Response> 지하철역_생성하기(StationRequest stationRequest) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(stationRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
		return response;
	}

	void 지하철역_생성됨(ExtractableResponse<Response> response, StationRequest stationRequest) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
		assertThat(response.body().jsonPath().getString("name")).isEqualTo(stationRequest.getName());
	}

	ExtractableResponse<Response> 지하철역_생성되어_있음(StationRequest stationRequest) {
		ExtractableResponse<Response> response = 지하철역_생성하기(stationRequest);
		지하철역_생성됨(response, stationRequest);
		return response;
	}

	void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createResponses) {
		List<Long> expectedLineIds = createResponses.stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	ExtractableResponse<Response> 지하철_역_목록_조회하기() {
		return RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();
	}

	ExtractableResponse<Response> 지하철_역_삭제하기(Long id) {
		return RestAssured.given().log().all()
			.when()
			.delete("/stations/" + id)
			.then().log().all()
			.extract();
	}

	void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	void 지하철_역_삭제_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

}
