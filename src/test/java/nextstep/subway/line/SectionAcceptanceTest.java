package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
	private Long stationId1;
	private Long stationId2;
	private int distance;
	private String lineId;

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
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));

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
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_역_상행_종점으로_구간_추가_요청
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId3, stationId1, 4));

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
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_역_하행_종점으로_구간_추가_요청
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId2, stationId3, 4));

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
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_구간_추가_요청(구간 사이 거리가 기존 등록한 거리보다 크거나 같음)
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 8));

		// then
		// 에러 발생
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("상행역과 하행역이 이미 노선에 등록되어 있으면 추가할 수 없다.")
	@Test
	void stationThatIsAlreadyRegisteredCantBeRegistered() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_구간_추가_요청(상행역, 하행역이 이미 노선에 존재)
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId2, 5));

		// then
		// 에러_발생
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("상행역과 하행역 둘 중 하나라도 포함되어 있지 않으면 등록할 수 없다.")
	@Test
	void stationsInSectionMustHaveOneStationInLine() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음

		// when
		// 새로운_지하철_역_추가
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));

		Long stationId4 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("선릉역")));

		// 지하철_구간_추가_요청(상행역과 하행역 둘 중 하나도 포함되어 있지 않음)
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId3, stationId4, 5));

		// then
		// 에러_발생
		assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("두 역 사이의 역을 삭제한다.")
	@Test
	void deleteStationBetweenStations() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음
		// 지하철_역(사이(between)_역)_등록되어 있음
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));

		// when
		// 두_역_사이의_역을_삭제_요청
		ExtractableResponse<Response> removeResponse = SectionAcceptanceMethod.removeStation(lineId, stationId3);

		// then
		// 지하철_역_삭제_됨
		assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_삭제_조회
		ExtractableResponse<Response> findResponse = LineAcceptanceMethod.findLine(lineId);
		assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("교대역", "역삼역");
	}

	@DisplayName("상행 종점 역을 삭제한다.")
	@Test
	void deleteUpStation() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음
		// 지하철_역(사이(between)_역)_등록되어 있음
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));

		// when
		// 상행_종점_역을_삭제_요청
		ExtractableResponse<Response> removeResponse = SectionAcceptanceMethod.removeStation(lineId, stationId1);

		// then
		// 지하철_역_삭제_됨
		assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_삭제_조회
		ExtractableResponse<Response> findResponse = LineAcceptanceMethod.findLine(lineId);
		assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("강남역", "역삼역");
	}

	@DisplayName("하행 종점 역을 삭제한다.")
	@Test
	void deleteDownStation() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음
		// 지하철_역(사이(between)_역)_등록되어 있음
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));

		// when
		// 하행_종점_역을_삭제_요청
		ExtractableResponse<Response> removeResponse = SectionAcceptanceMethod.removeStation(lineId, stationId2);

		// then
		// 지하철_역_삭제_됨
		assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_삭제_조회
		ExtractableResponse<Response> findResponse = LineAcceptanceMethod.findLine(lineId);
		assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("교대역", "강남역");
	}

	@DisplayName("노선에 없는 역을 삭제한다.")
	@Test
	void deleteNotExistStation() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음
		// 지하철_역(사이(between)_역)_등록되어 있음
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponse = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));
		// 다른_역_등록
		Long stationId4 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("신촌역")));

		// when
		// 다른_역을_삭제_요청
		ExtractableResponse<Response> removeResponse = SectionAcceptanceMethod.removeStation(lineId, stationId4);

		// then
		// 에러 발생
		assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("구간이 하나일 때 상행 종점 혹은 하행 종점을 삭제한다.")
	@Test
	void deleteTerminalStationAndSectionIsOnlyOne() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음

		// when
		// 상행_종점_삭제_요청
		ExtractableResponse<Response> removeResponse1 = SectionAcceptanceMethod.removeStation(lineId, stationId1);

		// 하행_종점_삭제_요청
		ExtractableResponse<Response> removeResponse2 = SectionAcceptanceMethod.removeStation(lineId, stationId2);

		// then
		// 에러_발생
		assertThat(removeResponse1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(removeResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("다른 노선에 역이 존재할 때 해당 노선의 역만 삭제한다.(환승역)")
	@Test
	void deleteTransferStation() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음
		// 지하철_역(사이(between)_역)_등록되어 있음
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponseStationOnGreen = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));
		// 등록한_지하철_역이_포함된_노선_등록
		Long stationId4 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("고속터미널역")));
		Long stationId5 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("남부터미널역")));
		distance = 10;

		ExtractableResponse<Response> createOrangeResponse = LineAcceptanceMethod.createLine(
			new LineRequest("3호선", "bg-orange-600", stationId4, stationId5, distance));
		String lineOrangeId = LineAcceptanceMethod.getLineID(createOrangeResponse);
		ExtractableResponse<Response> addResponseStationOnOrange = SectionAcceptanceMethod.addSection(lineOrangeId,
			new SectionRequest(stationId4, stationId3, 3));

		// when
		// 환승역_모든_노선에_대해_삭제_요청
		ExtractableResponse<Response> removeResponse = SectionAcceptanceMethod.removeStation(lineId, stationId3);

		// then
		// 지하철_역_삭제_됨
		assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_삭제_조회
		ExtractableResponse<Response> findGreenResponse = LineAcceptanceMethod.findLine(lineId);
		ExtractableResponse<Response> findOrangeResponse = LineAcceptanceMethod.findLine(lineOrangeId);
		assertThat(findGreenResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("교대역", "역삼역");
		assertThat(findOrangeResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("고속터미널역", "강남역", "남부터미널역");
	}

	@DisplayName("다른 노선에 역이 존재할 때 모든 노선에 대해 역을 삭제한다.(환승역)")
	@Test
	void deleteTransferStationOnAllLines() {
		// given
		// 지하철_역_등록되어_있음
		// 지하철_노선_등록되어_있음
		// 지하철_역(사이(between)_역)_등록되어 있음
		Long stationId3 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("강남역")));
		ExtractableResponse<Response> addResponseStationOnGreen = SectionAcceptanceMethod.addSection(lineId,
			new SectionRequest(stationId1, stationId3, 4));
		// 등록한_지하철_역이_포함된_노선_등록
		Long stationId4 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("고속터미널역")));
		Long stationId5 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("남부터미널역")));
		distance = 10;

		ExtractableResponse<Response> createOrangeResponse = LineAcceptanceMethod.createLine(
			new LineRequest("3호선", "bg-orange-600", stationId4, stationId5, distance));
		String lineOrangeId = LineAcceptanceMethod.getLineID(createOrangeResponse);
		ExtractableResponse<Response> addResponseStationOnOrange = SectionAcceptanceMethod.addSection(lineOrangeId,
			new SectionRequest(stationId4, stationId3, 3));

		// when
		// 환승역_모든_노선에_대해_삭제_요청
		ExtractableResponse<Response> removeResponse = SectionAcceptanceMethod.removeStation(stationId3);

		// then
		// 지하철_역_삭제_됨
		assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		// 지하철_삭제_조회
		ExtractableResponse<Response> findGreenResponse = LineAcceptanceMethod.findLine(lineId);
		ExtractableResponse<Response> findOrangeResponse = LineAcceptanceMethod.findLine(lineOrangeId);
		assertThat(findGreenResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("교대역", "역삼역");
		assertThat(findOrangeResponse.jsonPath().getObject(".", LineResponse.class).getStations()
			.stream().map(it -> it.getName()).collect(Collectors.toList())).containsExactly("고속터미널역", "남부터미널역");
	}
}
