package nextstep.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선의 섹션 관련 기능(구간 등록 시 예외 케이스를 고려하기)")
public class SectionExceptionAcceptanceTest extends AcceptanceTest {

	StationResponse 강남역;

	StationResponse 광교역;

	LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = postStation(new StationRequest("강남역"));
		광교역 = postStation(new StationRequest("광교역"));

		LineRequest createParams = new LineRequest(
			"신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 7
		);

		신분당선 = postLine(createParams);
	}

	/**
	 * {@link nextstep.subway.line.ui.LineController#addSection(Long, SectionRequest)}
	 */
	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음. 강남역--7--광교역, (B)--7--광교역")
	@Test
	void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
		// given
		StationResponse B역 = postStation(new StationRequest("B"));
		int distance = 4;

		// when
		// 지하철_노선에_지하철역_등록_요청
		postSection(신분당선, new SectionRequest(강남역.getId(), B역.getId(), distance));

		// then
		// 지하철_노선에_지하철역_등록됨
		assert false;
	}

	@DisplayName("상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음. 강남역--7--광교역, 강남역--7--광교역")
	@Test
	void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
		assert false;
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음. 강남역--3--A--4--광교역, B--7--C")
	void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
		assert false;
	}

}
