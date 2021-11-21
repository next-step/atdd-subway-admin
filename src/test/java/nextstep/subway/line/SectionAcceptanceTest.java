package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.testFactory.AcceptanceTestFactory;

@DisplayName("구간 등록 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	public static final String SECTION_SERVICE_PATH = "/sections";
	private static final String STATION_QUERY_PARAMETER_PATH = "stationId";

	private StationResponse 강남역;
	private StationResponse 광교역;
	private StationResponse 성복역;
	private StationResponse 수지구청역;
	private StationResponse 신논현;
	private StationResponse 호매실역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = StationTestFactory.지하철역_생성(StationAcceptanceTest.강남역_정보).as(StationResponse.class);
		광교역 = StationTestFactory.지하철역_생성(StationAcceptanceTest.광교역_정보).as(StationResponse.class);
		성복역 = StationTestFactory.지하철역_생성(StationTestFactory.지하철역_이름_정의("성복역"))
			.as(StationResponse.class);
		수지구청역 = StationTestFactory.지하철역_생성(StationTestFactory.지하철역_이름_정의("수지구청역"))
			.as(StationResponse.class);
		신논현 = StationTestFactory.지하철역_생성(StationTestFactory.지하철역_이름_정의("신논현"))
			.as(StationResponse.class);
		호매실역 = StationTestFactory.지하철역_생성(StationTestFactory.지하철역_이름_정의("호매실"))
			.as(StationResponse.class);

		Map<String, String> 신분당선_정보 = LineTestFactory.지하철_노선_정보_정의("신분당선", "bg-red-600",
			강남역.getId(), 광교역.getId(), 10);
		신분당선 = LineTestFactory.지하철_노선_생성(신분당선_정보).as(LineResponse.class);
	}

	@DisplayName("노선 중간에 구간을 등록한다.(상행역과 맞닿음)")
	@Test
	void addSection() {

		// when
		Map<String, String> 강남_성복_구간_정보 = 구간_정보_정의(강남역.getId(), 성복역.getId(), 5);
		ExtractableResponse<Response> 강남_성복_구간_등록_결과 = 지하철_노선에_구간_등록_요청(강남_성복_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.정상_생성_확인(강남_성복_구간_등록_결과);
		지하철_노선_구간_순서_확인(강남_성복_구간_등록_결과, Arrays.asList(강남역, 성복역, 광교역));
	}

	@DisplayName("노선 중간에 구간을 등록한다.(하행역과 맞닿음)")
	@Test
	void addSection2() {

		// when
		Map<String, String> 성복_광교_구간_정보 = 구간_정보_정의(성복역.getId(), 광교역.getId(), 7);
		ExtractableResponse<Response> 성복_광교_구간_등록_결과 = 지하철_노선에_구간_등록_요청(성복_광교_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.정상_생성_확인(성복_광교_구간_등록_결과);
		지하철_노선_구간_순서_확인(성복_광교_구간_등록_결과, Arrays.asList(강남역, 성복역, 광교역));
	}

	@DisplayName("상행 종점 앞에 구간 추가")
	@Test
	void addSection3() {

		// when
		Map<String, String> 신논현_강남_구간_정보 = 구간_정보_정의(신논현.getId(), 강남역.getId(), 12);
		ExtractableResponse<Response> 신논현_강남_구간_등록_결과 = 지하철_노선에_구간_등록_요청(신논현_강남_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.정상_생성_확인(신논현_강남_구간_등록_결과);
		지하철_노선_구간_순서_확인(신논현_강남_구간_등록_결과, Arrays.asList(신논현, 강남역, 광교역));
	}

	@DisplayName("하행 종점 뒤에 구간 추가")
	@Test
	void addSection4() {

		// when
		Map<String, String> 광교_호매실_구간_정보 = 구간_정보_정의(광교역.getId(), 호매실역.getId(), 15);
		ExtractableResponse<Response> 광교_호매실_구간_등록_결과 = 지하철_노선에_구간_등록_요청(광교_호매실_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.정상_생성_확인(광교_호매실_구간_등록_결과);
		지하철_노선_구간_순서_확인(광교_호매실_구간_등록_결과, Arrays.asList(강남역, 광교역, 호매실역));
	}

	@DisplayName("기존 역 사이 길이보다 크거나 같으면 등록할 수 없음")
	@Test
	void addSection_exception1() {

		// when
		Map<String, String> 강남_성복_구간_정보 = 구간_정보_정의(강남역.getId(), 성복역.getId(), 10);
		ExtractableResponse<Response> 강남_성복_구간_등록_결과 = 지하철_노선에_구간_등록_요청(강남_성복_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.예외_발생_확인(강남_성복_구간_등록_결과);
	}

	@DisplayName("상행역_하행역_모두_등록되어있으면_추가할_수_없음")
	@Test
	void addSection_exception2() {
		// given
		Map<String, String> 강남_성복_구간_정보 = 구간_정보_정의(강남역.getId(), 성복역.getId(), 5);
		지하철_노선에_구간_등록_요청(강남_성복_구간_정보, getSectionInLinePath(신분당선.getId()));

		// when
		ExtractableResponse<Response> 강남_성복_구간_등록_결과 = 지하철_노선에_구간_등록_요청(강남_성복_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.예외_발생_확인(강남_성복_구간_등록_결과);
	}

	@DisplayName("상행역_하행역_모두_기존에_없는_경우")
	@Test
	void addSection_exception3() {

		// when
		Map<String, String> 성복_수지구청_구간_정보 = 구간_정보_정의(성복역.getId(), 수지구청역.getId(), 7);
		ExtractableResponse<Response> 성복_수지구청_구간_등록_결과 = 지하철_노선에_구간_등록_요청(성복_수지구청_구간_정보,
			getSectionInLinePath(신분당선.getId()));

		// then
		AcceptanceTestFactory.예외_발생_확인(성복_수지구청_구간_등록_결과);
	}

	@DisplayName("상행 종점 삭제")
	@Test
	void removeSection1() {
		// given
		지하철_노선에_구간_등록_요청(구간_정보_정의(강남역.getId(), 수지구청역.getId(), 4), getSectionInLinePath(신분당선.getId()));
		지하철_노선에_구간_등록_요청(구간_정보_정의(수지구청역.getId(), 성복역.getId(), 2), getSectionInLinePath(신분당선.getId()));

		// when
		ExtractableResponse<Response> 삭제_결과 = 지하철_노선_구간의_지하철역_삭제_요청(
			getDeleteStationInLinePath(신분당선.getId(), 강남역.getId()));

		// then
		AcceptanceTestFactory.삭제_완료_확인(삭제_결과);
	}

	@DisplayName("하행 종점 삭제")
	@Test
	void removeSection2() {
		// given
		지하철_노선에_구간_등록_요청(구간_정보_정의(강남역.getId(), 수지구청역.getId(), 4), getSectionInLinePath(신분당선.getId()));
		지하철_노선에_구간_등록_요청(구간_정보_정의(수지구청역.getId(), 성복역.getId(), 2), getSectionInLinePath(신분당선.getId()));

		// when
		ExtractableResponse<Response> 삭제_결과 = 지하철_노선_구간의_지하철역_삭제_요청(
			getDeleteStationInLinePath(신분당선.getId(), 강남역.getId()));

		// then
		AcceptanceTestFactory.삭제_완료_확인(삭제_결과);
	}

	@DisplayName("구간 내 역 삭제")
	@Test
	void removeSection3() {
		// given
		지하철_노선에_구간_등록_요청(구간_정보_정의(강남역.getId(), 수지구청역.getId(), 4), getSectionInLinePath(신분당선.getId()));
		지하철_노선에_구간_등록_요청(구간_정보_정의(수지구청역.getId(), 성복역.getId(), 2), getSectionInLinePath(신분당선.getId()));

		// when
		ExtractableResponse<Response> 삭제_결과 = 지하철_노선_구간의_지하철역_삭제_요청(
			getDeleteStationInLinePath(신분당선.getId(), 강남역.getId()));

		// then
		AcceptanceTestFactory.삭제_완료_확인(삭제_결과);
	}

	private void 지하철_노선_구간_순서_확인(ExtractableResponse<Response> 강남_성복_구간_등록_결과, List<StationResponse> expectedStations) {
		List<Station> stations = 강남_성복_구간_등록_결과.jsonPath()
			.getList("stations", Station.class);
		for (int i = 0; i < stations.size(); i++) {
			Station station = stations.get(i);
			StationResponse expectedStation = expectedStations.get(i);
			assertThat(station.getId()
				.equals(expectedStation.getId()))
				.isTrue();
		}
	}

	private ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Map<String, String> params, String path) {
		return AcceptanceTestFactory.post(params, path);
	}

	private ExtractableResponse<Response> 지하철_노선_구간의_지하철역_삭제_요청(String path) {
		return AcceptanceTestFactory.delete(path);
	}

	private Map<String, String> 구간_정보_정의(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));
		return params;
	}

	private String getSectionInLinePath(Long lineId) {
		return String.join("", LineAcceptanceTest.LINE_SERVICE_PATH, "/", String.valueOf(lineId),
			SECTION_SERVICE_PATH);
	}

	private String getDeleteStationInLinePath(Long lineId, Long stationId) {
		return String.join("", LineAcceptanceTest.LINE_SERVICE_PATH, "/", String.valueOf(lineId),
			SECTION_SERVICE_PATH, "?", STATION_QUERY_PARAMETER_PATH, String.valueOf(stationId));
	}
}
