package nextstep.subway.station.acceptance;

import static nextstep.subway.station.acceptance.LineSteps.*;
import static nextstep.subway.station.acceptance.StationSteps.지하철_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	private StationResponse 강남역;
	private StationResponse 광교역;
	private StationResponse 정자역;
	private LineResponse 신분당선;

	@BeforeEach
	void init() {
		// given
		강남역 = 지하철_생성_요청("강남역").as(StationResponse.class);
		광교역 = 지하철_생성_요청("광교역").as(StationResponse.class);
		정자역 = 지하철_생성_요청("정자역").as(StationResponse.class);
		신분당선 = 노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 30).as(LineResponse.class);
	}

	/**
	 *  Given 지하철 노선에 구간을 생성하고
	 *	When 기존 구간 사이에 새로운 구간을 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 * 	Then 지하철역 목록 조회 시 생성한 역들을 찾을 수 있다.
	 */
	@DisplayName("기존 구간 사이에 새로운 구간을 생성한다.")
	@Test
	void addSectionInMiddle() {
		// when
		구간_생성_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 10);
		ExtractableResponse<Response> response = 노선_조회_요청(신분당선.getId());

		// then
		요청_응답_확인(response, HttpStatus.OK);
		지하철노선_역_목록_확인(response, 강남역.getName(), 정자역.getName(), 광교역.getName());
	}

	/**
	 *  Given 지하철 노선에 구간을 생성하고
	 *	When 기존 구간 앞 상행역에 새로운 구간을 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 * 	Then 지하철역 목록 조회 시 생성한 역들을 찾을 수 있다.
	 */
	@DisplayName("기존 구간 앞에 새로운 구간을 생성한다.")
	@Test
	void addSectionInFrontOfUpStation() {
		// when
		구간_생성_요청(신분당선.getId(), 정자역.getId(), 강남역.getId(), 10);
		ExtractableResponse<Response> response = 노선_조회_요청(신분당선.getId());

		// then
		요청_응답_확인(response, HttpStatus.OK);
		지하철노선_역_목록_확인(response, 정자역.getName(), 강남역.getName(), 광교역.getName());
	}

	/**
	 *  Given 지하철 노선에 구간을 생성하고
	 *	When 기존 구간 뒤 하행역에 새로운 구간을 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 * 	Then 지하철역 목록 조회 시 생성한 역들을 찾을 수 있다.
	 */
	@DisplayName("기존 구간 뒤에 새로운 구간을 생성한다.")
	@Test
	void addSectionBehindDownStation() {
		// when
		구간_생성_요청(신분당선.getId(), 광교역.getId(), 정자역.getId(), 10);
		ExtractableResponse<Response> response = 노선_조회_요청(신분당선.getId());

		// then
		요청_응답_확인(response, HttpStatus.OK);
		지하철노선_역_목록_확인(response, 강남역.getName(), 광교역.getName(), 정자역.getName());
	}

	private void 지하철노선_역_목록_확인(ExtractableResponse<Response> response, String ...stationNames) {
		assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly(stationNames);
	}
}
