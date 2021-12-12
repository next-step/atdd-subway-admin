package nextstep.subway.section;

import static nextstep.subway.AcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선에 상행 종점에 역을 추가한다.")
	@Test
	void addSection_success1() {
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station upStation = new Station(선릉역.getId(), 선릉역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Station upperStation = new Station(삼성역.getId(), 삼성역.getName());
		Section section = new Section(upStation, downStation, 5);

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(upperStation.getId(), upStation.getId(), 10);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upperStation, upStation, downStation));
	}

	@DisplayName("지하철 노선에 하행 종점에 역을 추가한다.")
	@Test
	void addSection_success2() {
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station downStation = new Station(선릉역.getId(), 선릉역.getName());
		Station underDownStation = new Station(역삼역.getId(), 역삼역.getName());
		Section section = new Section(upStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest sectionRequest = new AddSectionRequest(downStation.getId(), underDownStation.getId(), 10);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, downStation, underDownStation));
	}

	@DisplayName("지하철 노선 구간 상행역 아래에 역을 추가한다. (A-C 구간에 A-B를 추가)")
	@Test
	void addSection_success3() {
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Station underUpStation = new Station(선릉역.getId(), 선릉역.getName());
		Section section = new Section(upStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest sectionRequest = new AddSectionRequest(upStation.getId(), underUpStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, underUpStation, downStation));
	}

	@DisplayName("지하철 노선 구간 하행역 위에 역을 추가한다. (A-C 구간에 B-C를 추가)")
	@Test
	void addSection_success4() {
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Station aboveDownStation = new Station(선릉역.getId(), 선릉역.getName());
		Section section = new Section(upStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest sectionRequest = new AddSectionRequest(aboveDownStation.getId(), downStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, aboveDownStation, downStation));
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@Test
	void addSection_failed1() {
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Station underUpStation = new Station(선릉역.getId(), 선릉역.getName());
		Section section = new Section(upStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest sectionRequest = new AddSectionRequest(upStation.getId(), underUpStation.getId(), 10);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.BAD_REQUEST);
		응답_메세지_검증(response, "추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void addSection_failed2() {
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station downStation = new Station(선릉역.getId(), 선릉역.getName());
		Section section = new Section(upStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest sectionRequest = new AddSectionRequest(upStation.getId(), downStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.BAD_REQUEST);
		응답_메세지_검증(response, "상행역과 하행역이 모두 노선 구간으로 등록되어 있습니다.");
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void addSection_failed3() {
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		StationResponse 강남역 = StationAcceptanceTest.지하철역_생성되어_있음_강남역();
		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Station otherStation = new Station(선릉역.getId(), 선릉역.getName());
		Station anotherStation = new Station(강남역.getId(), 강남역.getName());
		Section section = new Section(upStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest sectionRequest = new AddSectionRequest(otherStation.getId(), anotherStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.BAD_REQUEST);
		응답_메세지_검증(response, "등록하려는 구간의 상행역과 하행역이 현재 노선 구간에 포함되어 있지 않습니다.");
	}

	@DisplayName("2개의 구간이 등록된 노선에서 상행 종점 구간을 삭제한다.")
	@Test
	void deleteSection_success1() {
		//given
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();

		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station midStation = new Station(선릉역.getId(), 선릉역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Section 삼성_선릉_구간 = new Section(upStation, midStation, 5);
		Section 선릉_역삼_구간 = new Section(midStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(삼성_선릉_구간);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest addSectionRequest = new AddSectionRequest(선릉_역삼_구간.getUpStation().getId(),
			선릉_역삼_구간.getDownStation().getId(), 선릉_역삼_구간.getDistance().get());

		구간_추가_요청(lineId, addSectionRequest);
		LineAcceptanceTest.지하철_노선_조회_요청(lineId);

		// when
		Map<String, Long> params = new HashMap<>();
		params.put("stationId", upStation.getId());
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.params(params)
			.when()
			.delete("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(midStation, downStation));
	}

	@DisplayName("2개의 구간이 등록된 노선에서 중간 구간을 삭제한다.")
	@Test
	void deleteSection_success2() {
		//given
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();

		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station midStation = new Station(선릉역.getId(), 선릉역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Section 삼성_선릉_구간 = new Section(upStation, midStation, 5);
		Section 선릉_역삼_구간 = new Section(midStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(삼성_선릉_구간);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest addSectionRequest = new AddSectionRequest(선릉_역삼_구간.getUpStation().getId(),
			선릉_역삼_구간.getDownStation().getId(), 선릉_역삼_구간.getDistance().get());

		구간_추가_요청(lineId, addSectionRequest);
		LineAcceptanceTest.지하철_노선_조회_요청(lineId);

		// when
		Map<String, Long> params = new HashMap<>();
		params.put("stationId", midStation.getId());
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.params(params)
			.when()
			.delete("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, downStation));
	}

	@DisplayName("2개의 구간이 등록된 노선에서 하행 종점 구간을 삭제한다.")
	@Test
	void deleteSection_success3() {
		//given
		StationResponse 삼성역 = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();

		Station upStation = new Station(삼성역.getId(), 삼성역.getName());
		Station midStation = new Station(선릉역.getId(), 선릉역.getName());
		Station downStation = new Station(역삼역.getId(), 역삼역.getName());
		Section 삼성_선릉_구간 = new Section(upStation, midStation, 5);
		Section 선릉_역삼_구간 = new Section(midStation, downStation, 5);

		ExtractableResponse<Response> createdLineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(삼성_선릉_구간);
		Long lineId = getLineIdFromCreatedLineResponse(createdLineResponse);
		AddSectionRequest addSectionRequest = new AddSectionRequest(선릉_역삼_구간.getUpStation().getId(),
			선릉_역삼_구간.getDownStation().getId(), 선릉_역삼_구간.getDistance().get());

		구간_추가_요청(lineId, addSectionRequest);
		LineAcceptanceTest.지하철_노선_조회_요청(lineId);

		// when
		Map<String, Long> params = new HashMap<>();
		params.put("stationId", downStation.getId());
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.params(params)
			.when()
			.delete("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(midStation, downStation));
	}

	private void 지하철역_순서_검증(ExtractableResponse<Response> lineResponse, List<Station> expected) {
		assertThat(expected).isEqualTo(
			lineResponse.jsonPath().getList("stations", Station.class));
	}

	private ExtractableResponse<Response> 구간_추가_요청(Long lineId, AddSectionRequest sectionRequest) {
		return 구간_추가_요청("/lines/" + lineId + "/sections", sectionRequest);
	}

	private ExtractableResponse<Response> 구간_추가_요청(String url, AddSectionRequest sectionRequest) {
		return RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(url)
			.then().log().all()
			.extract();
	}
}
