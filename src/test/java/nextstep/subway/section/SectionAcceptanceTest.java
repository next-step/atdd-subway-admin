package nextstep.subway.section;

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
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
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
		지하철_노선에_구간_포함됨(response, Arrays.asList("성수역", "강남역", "사당역", "서울대입구역"));

	}

	@DisplayName("노선에 구간을 앞에 등록한다.")
	@Test
	void 노선에_구간_앞에_등록한다() {
		// when
		Long ddukSumStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("뚝섬역"));
		SectionRequest sectionRequest = new SectionRequest(ddukSumStationId, sungSuStationId, 1);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		지하철_노선에_구간_포함됨(response, Arrays.asList("뚝섬역", "성수역", "강남역", "사당역"));
	}

	@DisplayName("노선에 구간을 중간에 등록한다.역 - 기존 하행역 새로운 상행역")
	@Test
	void 노선에_구간_중간에_등록한다_기존_하행역_새로운_상행역() {
		// when
		Long gunDaeStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("건대입구역"));
		SectionRequest sectionRequest = new SectionRequest(gunDaeStationId, gangNamStationId, 2);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		지하철_노선에_구간_포함됨(response, Arrays.asList("성수역", "건대입구역", "강남역", "사당역"));
	}

	@DisplayName("노선에 구간을 중간에 등록한다.역 - 기존 상행역 새로운 하행역")
	@Test
	void 노선에_구간_중간에_등록한다_기존_상역_새로운_하행역() {
		// when
		Long gangByunStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("강변역"));
		SectionRequest sectionRequest = new SectionRequest(sungSuStationId, gangByunStationId, 2);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		지하철_노선에_구간_포함됨(response, Arrays.asList("성수역", "강변역", "강남역", "사당역"));
	}

	@DisplayName("새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
	@Test
	void 노선에_구간_중간에_등록한다_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없다() {
		// when
		Long gangByunStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("강변역"));
		SectionRequest sectionRequest = new SectionRequest(sungSuStationId, gangByunStationId, 15);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		지하철_노선에_구간_등록_실패(response);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
	@Test
	void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없다() {
		// when
		Long gangByunStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("강변역"));
		Long gunDaeStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("건대입구역"));
		SectionRequest sectionRequest = new SectionRequest(gunDaeStationId, gangByunStationId, 15);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		지하철_노선에_구간_등록_실패(response);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 추가할 수 없음")
	@Test
	void 상행역과_하행역이_이미_노선에_모두_등록되어_추가할_수_없음() {
		// when
		SectionRequest sectionRequest = new SectionRequest(sungSuStationId, gangNamStationId, 10);
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);
		// then
		지하철_노선에_구간_등록_실패(response);
	}

	@DisplayName("지하철 노선에 지하철역 중간의 역을 제거한다.")
	@Test
	void 지하철_노선에_지하철역_중간의_역을_제거한다() {
		//when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineNumber2Id, gangNamStationId);
		//then
		지하철_노선에_지하철역_제거_성공(response);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.단일_지하철_노선을_조회한다(lineNumber2Id);
		지하철_노선에_구간_포함됨(lineResponse, Arrays.asList("성수역", "사당역"));
	}

	@DisplayName("지하철 노선에 지하철역 가장 앞의 역을 제거한다.")
	@Test
	void 지하철_노선에_지하철역_가장_앞의_역을_제거한다() {
		//when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineNumber2Id, sungSuStationId);
		//then
		지하철_노선에_지하철역_제거_성공(response);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.단일_지하철_노선을_조회한다(lineNumber2Id);
		지하철_노선에_구간_포함됨(lineResponse, Arrays.asList("강남역", "사당역"));
	}

	@DisplayName("지하철 노선에 지하철역 가장 뒤의 역을 제거한다.")
	@Test
	void 지하철_노선에_지하철역_가장_뒤의_역을_제거한다() {
		//when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineNumber2Id, saDangStationId);
		//then
		지하철_노선에_지하철역_제거_성공(response);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.단일_지하철_노선을_조회한다(lineNumber2Id);
		지하철_노선에_구간_포함됨(lineResponse, Arrays.asList("성수역", "강남역"));
	}

	@DisplayName("지하철 노선에 지하철역 존재하지않는 역을 제거한다. - 제거 실패")
	@Test
	void 지하철_노선에_지하철역_존재하지않는_역을_제거한다_제거_실패() {
		//when
		Long seoulUnivStationId = StationAcceptanceTest.지하철역_생성되어_있음(new StationRequest("서울대입구역"));
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineNumber2Id, seoulUnivStationId);
		//then
		지하철_노선에_지하철역_제거_실패(response);
	}

	@DisplayName("지하철 노선에 지하철역 마지막 구간의 역을 제거한다. - 제거 실패")
	@Test
	void 지하철_노선에_지하철역_마지막_구간의_역을_제거한다_제거_실패() {
		//when
		ExtractableResponse<Response> sungSuDeleteResponse = 지하철_노선에_지하철역_제거_요청(lineNumber2Id, sungSuStationId);
		ExtractableResponse<Response> saDangDeleteResponse = 지하철_노선에_지하철역_제거_요청(lineNumber2Id, saDangStationId);
		//then
		지하철_노선에_지하철역_제거_성공(sungSuDeleteResponse);
		지하철_노선에_지하철역_제거_실패(saDangDeleteResponse);
	}

	private void 지하철_노선에_구간_등록_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

	private void 지하철_노선에_지하철역_제거_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철_노선에_지하철역_제거_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	ExtractableResponse<Response> 지하철_노선에_지하철역_제거_요청(Long lineId, Long stationId) {
		return RestAssured.given().log().all()
			.when()
			.delete("/lines/" + lineId + "/sections?stationId=" + stationId)
			.then().log().all()
			.extract();
	}

	public void 지하철_노선에_구간_포함됨(ExtractableResponse<Response> response, List<String> expectedStations) {
		List<Station> stations = response.body().jsonPath().getList("stations", Station.class);
		List<String> actualStations = stations.stream().map(station -> station.getName()).collect(Collectors.toList());
		assertThat(expectedStations).containsAll(actualStations);
	}

}
