package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineTestApi;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
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
		ExtractableResponse<Response> createdLine = LineTestApi
			  .지하철_노선과_구간정보가_등록되어_있음("2호선", "green", createdStation1.body().as(StationResponse.class),
					createdStation2.body().as(StationResponse.class), 10);
		LineResponse lineResponse = createdLine.body().as(LineResponse.class);
		SectionRequest request = new SectionRequest(
			  createdStation2.body().as(StationResponse.class).getId(),
			  createdStation3.body().as(StationResponse.class).getId(), 10);

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
		SectionResponse actual = response.body().as(SectionResponse.class);
		SectionResponse expected = new SectionResponse(request.getUpStationId(), request.getDownStationId(), request.getDistance());
		assertThat(actual.getUpStationId()).isEqualTo(expected.getUpStationId());
		assertThat(actual.getDownStationId()).isEqualTo(expected.getDownStationId());
		assertThat(actual.getDistance()).isEqualTo(expected.getDistance());
	}
}
