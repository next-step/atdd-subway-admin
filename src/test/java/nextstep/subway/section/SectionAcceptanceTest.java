package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private StationResponse 강남역;
	private StationResponse 광교역;
	private LineRequest lineRequest;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();
		// given
		강남역 = StationAcceptanceTest.지하철역을_생성요청("강남역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역을_생성요청("광교역").as(StationResponse.class);

		lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
		신분당선 = LineAcceptanceTest.지하철노선을_생성_요청(lineRequest).as(LineResponse.class);
	}

	@Test
	@DisplayName("노선에 구간을 등록한다")
	void testAddSection() {
		StationResponse 판교역 = StationAcceptanceTest.지하철역을_생성요청("판교역").as(StationResponse.class);
		SectionRequest sectionRequest = new SectionRequest(판교역.getId(), 광교역.getId(), 4);

		ExtractableResponse<Response> addSectionResponse = this.노선에_구간등록을_요청(신분당선.getId(), sectionRequest);
		LineResponse lineResponse = addSectionResponse.response().as(LineResponse.class);

		assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(lineResponse.getStations()).containsExactly(강남역, 판교역, 광교역);
	}

	@Test
	@DisplayName("노선에 구간을 제거한다")
	void testRemoveLineStation() {
		//given 노선 셋팅
		StationResponse 판교역 = StationAcceptanceTest.지하철역을_생성요청("판교역").as(StationResponse.class);
		SectionRequest sectionRequest = new SectionRequest(판교역.getId(), 광교역.getId(), 4);
		this.노선에_구간등록을_요청(신분당선.getId(), sectionRequest);

		//when 노선의 역제거
		ExtractableResponse<Response> removeLineStationResponse = this.노선의역_제거요청(신분당선.getId(), 판교역.getId());
		assertThat(removeLineStationResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 노선의역_제거요청(long lineId, long stationId) {
		return RestAssured.given().log().all()
			.queryParam("stationId", stationId)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 노선에_구간등록을_요청(long lineId, SectionRequest sectionRequest) {
		return RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();
	}
}
