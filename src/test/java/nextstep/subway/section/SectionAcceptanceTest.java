package nextstep.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선의 섹션 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

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
			"신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10
		);

		신분당선 = postLine(createParams);
	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {
		// when
		// 지하철_노선에_지하철역_등록_요청

		// then
		// 지하철_노선에_지하철역_등록됨
	}
}
