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
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선에 상행 종점에 역을 추가한다.")
	@Test
	void addSection1() {
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
	void addSection2() {
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

	@DisplayName("지하철 노선 구간 중간에 역을 추가한다.")
	@Test
	void addSection3() {
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
