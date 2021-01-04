package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> response = 지하철_생성_요청("강남역");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		지하철_생성_요청("강남역");

		// when
		ExtractableResponse<Response> response = 지하철_생성_요청("강남역");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		// given
		List<Long> 등록된역_ID_목록 = Stream.of(
			지하철_생성_요청("강남역"),
			지하철_생성_요청("역삼역")
		).map(this::location_header에서_ID_추출)
			.collect(Collectors.toList());

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("id", Long.class)).containsAll(등록된역_ID_목록)
		);
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		long 강남역_ID = location_header에서_ID_추출(지하철_생성_요청("강남역"));

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete("/stations/" + 강남역_ID)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private long location_header에서_ID_추출(ExtractableResponse<Response> extractableResponse) {
		return Long.parseLong(extractableResponse.header("Location").split("/")[2]);
	}

	private ExtractableResponse<Response> 지하철_생성_요청(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/stations")
			.then().log().all().extract();
	}
}