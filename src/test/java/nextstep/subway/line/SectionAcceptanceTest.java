package nextstep.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.StationAcceptanceMethod;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	Long stationId1;
	Long stationId2;
	int distance;

	private void setUpStations() {
		stationId1 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("교대역")));
		stationId2 = StationAcceptanceMethod.getStationID(StationAcceptanceMethod.createStations(
			new StationRequest("역삼역")));
		distance = 10;
	}

	@DisplayName("기존에 존재하던 역 사이에 새로운 역을 등록한다.")
	@Test
	void addSectionBetweenStations() {
		// given
		// 지하철_역_등록되어_있음
		setUpStations();

		// when
		// 지하철_구간_추가_요청

		// then
		// 지하철_구간_생성됨
	}

	@DisplayName("새로운 역을 상행 종점으로 등록한다.")
	@Test
	void addSectionAsUpStation() {
		// given
		// 지하철_역_등록되어_있음
		setUpStations();

		// when
		// 지하철_역_상행_종점으로_구간_추가_요청

		// then
		// 지하철_구간_생성됨
		// 지하철_상행_종점_확인
	}

	@DisplayName("새로운 역을 하행 종점으로 등록한다.")
	@Test
	void addSectionAsDownStation() {
		// given
		// 지하철_역_등록되어_있음
		setUpStations();

		// when
		// 지하철_역_하행_종점으로_구간_추가_요청

		// then
		// 지하철_구간_생성됨
		// 지하철_하행_종점_확인
	}

	@DisplayName("역 사이에 새로운 역 등록 시 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
	@Test
	void sectionLengthCantNotExceedTwoStationDistance() {
		// given
		// 지하철_역_등록되어_있음
		setUpStations();

		// when
		// 지하철_구간_추가_요청(구간 사이 거리가 기존 등록한 거리보다 크거나 같음)

		// then
		// 에러_발생
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
