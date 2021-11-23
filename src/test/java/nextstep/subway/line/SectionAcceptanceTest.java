package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private StationResponse 강남역;
	private StationResponse 광교역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = 지하철역이_등록되어있음(강남역_생성_요청값());
		광교역 = 지하철역이_등록되어있음(광교역_생성_요청값());

		신분당선 = 지하철_노선_등록되어_있음(신분당선_생성_요청값(강남역, 광교역));
	}

	@DisplayName("섹션으로 정거장리스트를 조회한다.")
	@Test
	void findAllStations() {
		// when
		List<StationResponse> 정거장리스트 = 신분당선.getStations();

		// then
		정거장리스트가_조회됨(정거장리스트);
	}

	void 정거장리스트가_조회됨(List<StationResponse> stationResponseList) {
		assertThat(stationResponseList).hasSize(2);
	}
}
