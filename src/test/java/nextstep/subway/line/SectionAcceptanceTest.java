package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

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
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceMethod;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	Long stationId1;
	Long stationId2;
	int distance;
	String lineId;

	@BeforeEach
	public void setUp() {
		super.setUp();

		setUpStations();
		setUpLine();
	}

	private void setUpStations() {
		stationId1 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("교대역")));
		stationId2 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("역삼역")));
		distance = 7;
	}

	private void setUpLine() {
		ExtractableResponse<Response> createResponse = LineAcceptanceMethod.createLine(
			new LineRequest("2호선", "bg-green-600", stationId1, stationId2, distance));
		lineId = LineAcceptanceMethod.getLineID(createResponse);
	}

	@DisplayName("기존에 존재하던 역 사이에 새로운 역을 등록한다.")
	@Test
	void addSectionBetweenStations() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_구간_추가_요청
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));

		ExtractableResponse<Response> addResponse = RestAssured
			.given().log().all()
			.body(new SectionRequest(stationId1, stationId3, 4))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines" + "/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		// 지하철_구간_생성됨
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_역_추가_조회
		ExtractableResponse<Response> findResponse = LineAcceptanceMethod.findLine(lineId);
		assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("교대역", "강남역", "역삼역");
	}

	@DisplayName("새로운 역을 상행 종점으로 등록한다.")
	@Test
	void addSectionAsUpStation() {
		// given
		// 지하철_역_등록되어_있음

		// when
		// 지하철_역_상행_종점으로_구간_추가_요청
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));

		ExtractableResponse<Response> addResponse = RestAssured
			.given().log().all()
			.body(new SectionRequest(stationId3, stationId1, 4))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines" + "/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		// 지하철_구간_생성됨
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_역_추가_조회
		ExtractableResponse<Response> findResponse = LineAcceptanceMethod.findLine(lineId);
		assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("강남역", "교대역", "역삼역");
	}

	@DisplayName("새로운 역을 하행 종점으로 등록한다.")
	@Test
	void addSectionAsDownStation() {
		// given
		// 지하철_역_등록되어_있음

		// when
		// 지하철_역_하행_종점으로_구간_추가_요청
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));

		ExtractableResponse<Response> addResponse = RestAssured
			.given().log().all()
			.body(new SectionRequest(stationId2, stationId3, 4))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines" + "/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		// 지하철_구간_생성됨
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_하행_종점_확인
		ExtractableResponse<Response> findResponse = LineAcceptanceMethod.findLine(lineId);
		assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("교대역", "역삼역", "강남역");
	}

	@DisplayName("역 사이에 새로운 역 등록 시 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
	@Test
	void sectionLengthCantNotExceedTwoStationDistance() {
		// given
		// 지하철_역_등록되어_있음

		// when
		// 지하철_구간_추가_요청(구간 사이 거리가 기존 등록한 거리보다 크거나 같음)
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));

		ExtractableResponse<Response> addResponse = RestAssured
			.given().log().all()
			.body(new SectionRequest(stationId1, stationId3, 8))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines" + "/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		// 에러 발생
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("상행역과 하행역이 이미 노선에 등록되어 있으면 추가할 수 없다.")
	@Test
	void stationThatIsAlreadyRegisteredCantBeRegistered() {
		// given
		// 지하철_역_등록되어_있음
		setUpStations();

		// when
		// 지하철_구간_추가_요청(상행역, 하행역이 이미 노선에 존재)

		// then
		// 에러_발생
	}

	@DisplayName("상행역과 하행역 둘 중 하나라도 포함되어 있지 않으면 등록할 수 없다.")
	@Test
	void stationsInSectionMustHaveOneStationInLine() {
		// given
		// 지하철_역_등록되어_있음
		setUpStations();

		// when
		// 지하철_구간_추가_요청(상행역과 하행역 둘 중 하나도 포함되어 있지 않음)

		// then
		// 에러_발생
	}
}
