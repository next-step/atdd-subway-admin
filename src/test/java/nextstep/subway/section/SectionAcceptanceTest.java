package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.ErrorResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class SectionAcceptanceTest extends AcceptanceTest {
	private StationResponse 강남역;

	private StationResponse 광교역;

	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();
		// given
		강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
		광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역");

		LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse response = 노선_생성_함수(lineRequest);

		String lineId = response.header("Location").split("/")[2];
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(lineId));
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		신분당선 = lineResponse;
	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {
		//새로운 역을 추가하고
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		//새로운역을 구간에 추가한다.
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(광교역.getId(), 새로운역.getId(), 3));

		//저장된 내용을 확인한다.
		String lineId = response.header("Location").split("/")[2];
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(lineId));

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName()).isEqualTo(신분당선.getName());

		//등록한 역 검증
		assertThat(lineResponse.getStations().size()).isEqualTo(3);
		assertThat(lineResponse.getStations())
				.extracting(SectionResponse::getId)
				.containsExactly(강남역.getId(), 광교역.getId(), 새로운역.getId());

	}

	ExtractableResponse<Response> 구간등록하기(SectionRequest section) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(section)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(String.format("/lines/%s/sections", String.valueOf(신분당선.getId())))
				.then().log().all()
				.extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		return response;
	}

	@DisplayName("새로운 역을 상행 종점으로 등록할 경우")
	@Test
	void addNewStationForUpTerminal() {
		//새로운역을 저장한다.
		StationResponse 새로운상행역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운상행역");
		//새로운역이 상행 종점이 되도록 구간을 등록한다.
		SectionRequest sectionRequest = new SectionRequest(새로운상행역.getId(), 강남역.getId(), 3);
		ExtractableResponse<Response> response = 구간등록하기(sectionRequest);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(신분당선.getId());
		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);

		assertThat(lineResponse.getName()).isEqualTo(신분당선.getName());
		assertThat(lineResponse.getStations().size()).isEqualTo(3);
		SectionResponse section = lineResponse.getStations().stream().filter(s -> s.getName().equalsIgnoreCase(새로운상행역.getName())).findAny().get();
		assertThat(section.getDistance()).isEqualTo(0);
	}

	@DisplayName("새로운 역을 하행점 종점으로 등록할 경우")
	@Test
	void addNewStationForDownTerminal() {
		//새로운 역을 등록한다.
		StationResponse 새로운하행역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운하행역");
		//새로운 역이 하행 종점이 되도록 구간을 등록한다.
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(광교역.getId(), 새로운하행역.getId(), 3));
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(신분당선.getId());
		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);

		assertThat(lineResponse.getName()).isEqualTo(신분당선.getName());
		assertThat(lineResponse.getStations().size()).isEqualTo(3);
		SectionResponse section = lineResponse.getStations().stream().filter(s -> s.getName().equalsIgnoreCase(새로운하행역.getName())).findAny().get();
		assertThat(section.getDistance()).isEqualTo(3);
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@Test
	void betweenAddSectionExceptionDistanceError() {
		//새로운 역을 등록한다.
		StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");
		//기존역 사이 길이보다 길도록 등록을 시도함.
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(강남역.getId(), 판교역.getId(), 15));
		//에러 발생
		assertThat(response.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(700);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void alreadyExistsUpDownStation() {
		//이미 구간으로 등록되어있는 역을 다시 구간등록한다.
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(강남역.getId(), 광교역.getId(), 15));
		//에러 발생
		assertThat(response.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(701);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void notIncludeLineBothStations() {
		//새로운 역 2개를 생성한다.
		StationResponse 청계산역 = StationAcceptanceTest.지하철역_등록되어_있음("청계산역");
		StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");
		//새로운 역 2개를 모두 구간 등록을 시도한다.
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(청계산역.getId(), 판교역.getId(), 15));
		//에러 발생
		assertThat(response.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(702);
	}

	@DisplayName("노선의 구간을 제거하는 기능,상행 종점 제거")
	@Test
	void deleteUpTerminal() {
		Long lineId = 신분당선.getId();
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(광교역.getId(), 새로운역.getId(), 3));
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		해당역의_구간을_삭제한다(lineId, 강남역.getId());

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(response.header("Location").split("/")[2]));

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations().size()).isEqualTo(2);
		assertThat(lineResponse.getStations())
				.extracting(SectionResponse::getId)
				.containsExactly(광교역.getId(), 새로운역.getId());
	}


	@DisplayName("노선의 구간을 제거하는 기능,하행 종점 제거")
	@Test
	void deleteDownTerminal() {
		Long lineId = 신분당선.getId();
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(광교역.getId(), 새로운역.getId(), 3));

		ExtractableResponse<Response> deleteResponse = 해당역의_구간을_삭제한다(lineId, 새로운역.getId());
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(response.header("Location").split("/")[2]));

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations().size()).isEqualTo(2);
		assertThat(lineResponse.getStations())
				.extracting(SectionResponse::getId)
				.containsExactly(강남역.getId(), 광교역.getId());
	}

	@DisplayName("노선의 구간을 제거하는 기능, 중간역 제거")
	@Test
	void deleteSectionIncludeBetweenStation() {
		Long lineId = 신분당선.getId();
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		ExtractableResponse<Response> response = 구간등록하기(new SectionRequest(광교역.getId(), 새로운역.getId(), 3));

		ExtractableResponse<Response> deleteResponse = 해당역의_구간을_삭제한다(lineId, 광교역.getId());
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(response.header("Location").split("/")[2]));

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations().size()).isEqualTo(2);
		assertThat(lineResponse.getStations())
				.extracting(SectionResponse::getId)
				.containsExactly(강남역.getId(), 새로운역.getId());
	}

	@DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때")
	@Test
	void deleteOneSection() {
		Long lineId = 신분당선.getId();
		ExtractableResponse<Response> deleteResponse = 해당역의_구간을_삭제한다(lineId, 강남역.getId());
		//에러 발생
		assertThat(deleteResponse.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(703);
	}

	@DisplayName("구노선에 등록되어있지 않은 역을 제거하려 한다.")
	@Test
	void deleteSectionForNotIncludeStation() {
		Long lineId = 신분당선.getId();
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		ExtractableResponse<Response> deleteResponse = 해당역의_구간을_삭제한다(lineId, 새로운역.getId());
		//에러 발생
		assertThat(deleteResponse.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(704);
	}

	private ExtractableResponse<Response> 해당역의_구간을_삭제한다(Long lineId, Long stationId) {
		ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
				.when()
				.delete(String.format("/%d/sections?stationId=%d", lineId, stationId))
				.then().log().all()
				.extract();
		return deleteResponse;
	}

}
