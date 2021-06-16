package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("노선에 구간 관리 기능 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

	Long sungSuStationId;
	Long gangNamStationId;
	Long saDangStationId;
	Long lineNumber2Id;

	@BeforeEach
	void sectionSetUp() {
		sungSuStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("성수역"));
		gangNamStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("강남역"));
		LineRequest lineNumber2 = new LineRequest("2호선", "Green", sungSuStationId, gangNamStationId, 10);
		lineNumber2Id = LineAcceptanceTest.지하철_노선_생성되어_있음(lineNumber2);
		saDangStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("사당역"));
		SectionRequest sectionRequest = new SectionRequest(gangNamStationId, saDangStationId, 5);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
	}

	@DisplayName("노선에 구간을 뒤에 등록한다.")
	@Test
	void 노선에_구간_뒤에_등록한다() {
		// when
		Long seoulDaeStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("서울대입구역"));
		SectionRequest sectionRequest = new SectionRequest(saDangStationId, seoulDaeStationId, 5);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		// 지하철_노선에_지하철역_등록됨
		assertThat(response.body().jsonPath().getString("stations[0].name")).isEqualTo("성수역");
		assertThat(response.body().jsonPath().getString("stations[1].name")).isEqualTo("강남역");
		assertThat(response.body().jsonPath().getString("stations[2].name")).isEqualTo("사당역");
		assertThat(response.body().jsonPath().getString("stations[3].name")).isEqualTo("서울대입구역");
	}

	@DisplayName("노선에 구간을 앞에 등록한다.")
	@Test
	void 노선에_구간_앞에_등록한다() {
		// when
		Long ddukSumStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("뚝섬역"));
		SectionRequest sectionRequest = new SectionRequest(ddukSumStationId, sungSuStationId, 1);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		// 지하철_노선에_지하철역_등록됨
		assertThat(response.body().jsonPath().getString("stations[0].name")).isEqualTo("뚝섬역");
		assertThat(response.body().jsonPath().getString("stations[1].name")).isEqualTo("성수역");
		assertThat(response.body().jsonPath().getString("stations[2].name")).isEqualTo("강남역");
		assertThat(response.body().jsonPath().getString("stations[3].name")).isEqualTo("사당역");
	}

	@DisplayName("노선에 구간을 중간에 등록한다.")
	@Test
	void 노선에_구간_중간에_등록한다() {
		// when
		Long gunDaeStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("건대입구역"));
		SectionRequest sectionRequest = new SectionRequest(gunDaeStationId, gangNamStationId, 2);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		// 지하철_노선에_지하철역_등록됨
		assertThat(response.body().jsonPath().getString("stations[0].name")).isEqualTo("성수역");
		assertThat(response.body().jsonPath().getString("stations[1].name")).isEqualTo("건대입구역");
		assertThat(response.body().jsonPath().getString("stations[2].name")).isEqualTo("강남역");
		assertThat(response.body().jsonPath().getString("stations[3].name")).isEqualTo("사당역");
	}

	ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(SectionRequest sectionRequest) {
		return RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + lineNumber2Id + "/sections")
			.then().log().all()
			.extract();
	}
}
