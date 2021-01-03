package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
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
		ExtractableResponse<Response> createdStation1 = StationTestApi.지하철_역_등록_요청("강남역");
		ExtractableResponse<Response> createdStation2 = StationTestApi.지하철_역_등록_요청("역삼역");
		ExtractableResponse<Response> createdStation3 = StationTestApi.지하철_역_등록_요청("선릉역");
		//지하철_노선과_구간정보가_등록되어_있음
		StationResponse station1 = createdStation1.body().as(StationResponse.class);
		StationResponse station2 = createdStation2.body().as(StationResponse.class);
		StationResponse station3 = createdStation3.body().as(StationResponse.class);

		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green",
					station1,
					station2, 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  station2.getId(),
			  station3.getId(), 10);

		//when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .body(request)
			  .when().post("/lines/" + lineResponse.getId() + "/sections")
			  .then().log().all()
			  .extract();

		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();

		List<StationResponse> stations = response.jsonPath()
			  .getList("stations", StationResponse.class);
		assertThat(stations).hasSize(3);
		assertThat(stations).containsExactly(station1, station2, station3);
	}

	@DisplayName("연결되지 않는 지하철 구간 정보를 추가한다.")
	@Test
	void addSectionWitUnlinkedSection() {
		//givne
		//지하철_역이_등록되어_있음
		ExtractableResponse<Response> createdStation1 = StationTestApi.지하철_역_등록_요청("강남역");
		ExtractableResponse<Response> createdStation2 = StationTestApi.지하철_역_등록_요청("역삼역");
		ExtractableResponse<Response> createdStation3 = StationTestApi.지하철_역_등록_요청("선릉역");
		ExtractableResponse<Response> createdStation4 = StationTestApi.지하철_역_등록_요청("삼성역");
		//지하철_노선과_구간정보가_등록되어_있음
		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green",
					createdStation1.body().as(StationResponse.class),
					createdStation2.body().as(StationResponse.class), 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  createdStation3.body().as(StationResponse.class).getId(),
			  createdStation4.body().as(StationResponse.class).getId(), 10);

		//when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			  .contentType(MediaType.APPLICATION_JSON_VALUE)
			  .body(request)
			  .when().post("/lines/" + lineResponse.getId() + "/sections")
			  .then().log().all()
			  .extract();

		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
