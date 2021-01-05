package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineTestApi;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationTestApi;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SectionsAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 구간 정보를 추가한다.")
	@Test
	void addSection() {
		//givne
		//지하철_역이_등록되어_있음
		Map<String, StationResponse> station = 지하철_역이_등록되어_있음("강남역", "역삼역", "선릉역");

		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green",
					station.get("강남역"),
					station.get("역삼역"), 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  station.get("역삼역").getId(),
			  station.get("선릉역").getId(), 10);

		//when
		ExtractableResponse<Response> response = 지하철_구간_정보를_등록한다(lineResponse.getId(), request);

		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();

		List<StationResponse> stations = response.jsonPath()
			  .getList("stations", StationResponse.class);
		assertThat(stations).hasSize(3);
		assertThat(stations)
			  .containsExactly(station.get("강남역"), station.get("역삼역"), station.get("선릉역"));
	}

	@DisplayName("연결되지 않는 지하철 구간 정보를 추가한다.")
	@Test
	void addSectionWitUnlinkedSection() {
		//givne
		//지하철_역이_등록되어_있음
		Map<String, StationResponse> station = 지하철_역이_등록되어_있음("강남역", "역삼역", "선릉역", "삼성역");
		//지하철_노선과_구간정보가_등록되어_있음
		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green",
					station.get("강남역"),
					station.get("역삼역"), 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  station.get("선릉역").getId(),
			  station.get("삼성역").getId(), 10);

		//when
		ExtractableResponse<Response> response = 지하철_구간_정보를_등록한다(lineResponse.getId(), request);

		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("구간정보를 삭제한다")
	@Test
	void delete() {
		//givne
		//지하철_역이_등록되어_있음
		Map<String, StationResponse> station = 지하철_역이_등록되어_있음("강남역", "역삼역", "선릉역");
		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green",
					station.get("강남역"),
					station.get("역삼역"), 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  station.get("역삼역").getId(),
			  station.get("선릉역").getId(), 10);
		지하철_구간_정보를_등록한다(lineResponse.getId(), request);

		Map<String, Long> params = new HashMap<>();
		params.put("stationId", station.get("역삼역").getId());

		//when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .params(params)
			  .when().delete("/lines/" + lineResponse.getId() + "/sections")
			  .then().log().all()
			  .extract();

		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("구간정보에서 등록되지 않은 역을 삭제한다.")
	@Test
	void delete2() {
		//givne
		//지하철_역이_등록되어_있음
		Map<String, StationResponse> station = 지하철_역이_등록되어_있음("강남역", "역삼역", "선릉역", "강변역");
		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green",
					station.get("강남역"),
					station.get("역삼역"), 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  station.get("역삼역").getId(),
			  station.get("선릉역").getId(), 10);
		지하철_구간_정보를_등록한다(lineResponse.getId(), request);

		Map<String, Long> params = new HashMap<>();
		params.put("stationId", station.get("강변역").getId());

		//when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .params(params)
			  .when().delete("/lines/" + lineResponse.getId() + "/sections")
			  .then().log().all()
			  .extract();

		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철_구간_정보를_등록한다(long lineId,
		  SectionRequest request) {
		return RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .body(request)
			  .when().post("/lines/" + lineId + "/sections")
			  .then().log().all()
			  .extract();
	}

	private Map<String, StationResponse> 지하철_역이_등록되어_있음(String... stationNames) {
		return Arrays.stream(stationNames)
			  .map(stationName -> {
				  ExtractableResponse<Response> stationResponse = StationTestApi
						.지하철_역_등록_요청(stationName);
				  return stationResponse.body().as(StationResponse.class);
			  }).collect(Collectors.toMap(StationResponse::getName, Function.identity()));
	}
}
