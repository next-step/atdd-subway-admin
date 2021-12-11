package nextstep.subway.section;

import static nextstep.subway.AcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

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

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선에 상행 종점에 역을 추가한다.")
	@Test
	void addSection_success1() {
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station upperStation = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
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
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station underDownStation = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		Section section = new Section(upStation, downStation, 5);

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(downStation.getId(), underDownStation.getId(), 10);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, downStation, underDownStation));
	}

	@DisplayName("지하철 노선 구간 상행역 아래에 역을 추가한다. (A-C 구간에 A-B를 추가)")
	@Test
	void addSection_success3() {
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station betweenStation = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		Section section = new Section(upStation, downStation, 5);

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(upStation.getId(), betweenStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, betweenStation, downStation));
	}

	@DisplayName("지하철 노선 구간 하행역 위에 역을 추가한다. (A-C 구간에 B-C를 추가)")
	@Test
	void addSection_success4() {
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station betweenStation = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		Section section = new Section(upStation, downStation, 5);

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(betweenStation.getId(), downStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.OK);
		ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(lineId);
		지하철역_순서_검증(lineResponse, Arrays.asList(upStation, betweenStation, downStation));
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@Test
	void addSection_failed1() {
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Station betweenStation = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		Section section = new Section(upStation, downStation, 5);

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(betweenStation.getId(), downStation.getId(), 10);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.BAD_REQUEST);
		응답_메세지_검증(response, "추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void addSection_failed2() {
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Section section = new Section(upStation, downStation, 5);

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(upStation.getId(), downStation.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.BAD_REQUEST);
		응답_메세지_검증(response, "상행역과 하행역이 모두 노선 구간으로 등록되어 있습니다.");
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void addSection_failed3() {
		Station upStation = StationAcceptanceTest.지하철역_생성되어_있음_삼성역();
		Station downStation = StationAcceptanceTest.지하철역_생성되어_있음_역삼역();
		Section section = new Section(upStation, downStation, 5);
		Station station1 = StationAcceptanceTest.지하철역_생성되어_있음_선릉역();
		Station station2 = StationAcceptanceTest.지하철역_생성되어_있음_강남역();

		LineAcceptanceTest.지하철_노선_등록되어_있음(section);
		Long lineId = 1L;
		AddSectionRequest sectionRequest = new AddSectionRequest(station1.getId(), station2.getId(), 3);

		ExtractableResponse<Response> response = 구간_추가_요청(lineId, sectionRequest);

		응답_검증(response, HttpStatus.BAD_REQUEST);
		응답_메세지_검증(response, "등록하려는 구간의 상행역과 하행역이 현재 노선 구간에 포함되어 있지 않습니다.");
	}

	private void 지하철역_순서_검증(ExtractableResponse<Response> lineResponse, List<Station> expected) {
		assertThat(expected).isEqualTo(
			lineResponse.jsonPath().getList("stations", Station.class));
	}

	private ExtractableResponse<Response> 구간_추가_요청(Long lineId, AddSectionRequest sectionRequest) {
		return RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();
	}
}
