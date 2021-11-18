package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.StationTestFactory;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.testFactory.AcceptanceTestFactory;

@DisplayName("구간 등록 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private StationResponse 강남역;
	private StationResponse 광교역;
	private LineResponse 신분당선;

	public static final String SECTION_SERVICE_PATH = "/sections";

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = StationTestFactory.지하철역_생성(StationAcceptanceTest.강남역_정보).as(StationResponse.class);
		광교역 = StationTestFactory.지하철역_생성(StationAcceptanceTest.광교역_정보).as(StationResponse.class);

		Map<String, String> 신분당선_정보 = LineTestFactory.지하철_노선_정보_정의("신분당선", "bg-red-600",
			강남역.getId(), 광교역.getId(), 10);
		신분당선 = LineTestFactory.지하철_노선_생성(신분당선_정보).as(LineResponse.class);
	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {
		// when
		Map<String, String> 성복역_정보 = StationTestFactory.지하철역_이름_정의("성복역");
		StationResponse 성복역 = StationTestFactory.지하철역_생성(성복역_정보).as(StationResponse.class);

		Map<String, String> 강남_성복_구간_정보 = 구간_정보_정의(강남역.getId(), 성복역.getId(), 5);
		ExtractableResponse<Response> 강남_성복_구간_등록_결과 = 지하철_노선에_구간_등록_요청(강남_성복_구간_정보,
			getLineSectionServicePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.정상_생성_확인(강남_성복_구간_등록_결과);
	}

	private ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Map<String, String> params, String path) {
		return AcceptanceTestFactory.post(params, path);
	}

	private Map<String, String> 구간_정보_정의(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));
		return params;
	}

	private String getLineSectionServicePath(Long lineId) {
		return String.join("", LineAcceptanceTest.LINE_SERVICE_PATH, "/", String.valueOf(lineId),
			SECTION_SERVICE_PATH);
	}
}
