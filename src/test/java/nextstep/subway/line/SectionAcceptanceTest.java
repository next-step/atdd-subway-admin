package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.SectionAcceptanceTestUtils.createSectionRequest;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성_요청_및_성공_체크;
import static nextstep.subway.line.LineAcceptanceTestHelper.createLineRequest;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청_및_성공_체크;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

	private static final String BASE_LINE_URL = "/lines";
	private static final String BASE_SECTION_URL = "/sections";

	private StationResponse 강남역;
	private StationResponse 역삼역;
	private StationResponse 구디역;
	private StationResponse 판교역;

	private LineResponse line_신분당선;

	@BeforeEach
	void setUpStations() {
		// given
		// 지하철 역 생성 요청
		강남역 = 지하철역_생성_요청_및_성공_체크("강남역");
		역삼역 = 지하철역_생성_요청_및_성공_체크("역삼역");
		구디역 = 지하철역_생성_요청_및_성공_체크("구디역");
		판교역 = 지하철역_생성_요청_및_성공_체크("판교역");

		// 지하철_노선_생성_요청
		LineRequest params = createLineRequest("신분당선", "bg-red-600", 강남역, 역삼역, 10);
		line_신분당선 = 지하철_노선_생성_요청_및_성공_체크(params);
	}

	@DisplayName("지하철 노선 구간을 등록한다. (새로운 역을 하행 종점으로 등록)")
	@Test
	void registSectionWithEnd() {
		// given
		SectionRequest request = createSectionRequest(역삼역, 구디역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선 구간을 등록한다. (새로운 역을 상행 종점으로 등록할 경우)")
	@Test
	void registSectionWithBegin() {
		// given
		SectionRequest request = createSectionRequest(구디역, 강남역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선 구간을 등록한다. (역 사이에 새로운 역을 등록할 경우, 상행에 연결)")
	@Test
	void registSectionWithConnectedBegin() {
		// given
		SectionRequest request = createSectionRequest(강남역, 구디역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선 구간을 등록한다. (역 사이에 새로운 역을 등록할 경우, 하행에 연결)")
	@Test
	void registSectionWithConnectedEnd() {
		// given
		SectionRequest request = createSectionRequest(구디역, 역삼역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("기존 역 사이 길이보다 큰 새로운 역을 등록한다.")
	@Test
	void registSectionWithBiggerThanOriginalDistance() {
		// given
		SectionRequest request = createSectionRequest(강남역, 구디역, 20);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("상행역과 하행역이 모두 등록되있는 새로운 역을 등록한다.")
	@Test
	void registerSectionWithAlreadyRegisterStations() {
		// given
		SectionRequest request = createSectionRequest(강남역, 역삼역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("상행역과 하행역이 모두 등록되어있지 않은 새로운 역을 등록한다.")
	@Test
	void registSectionNotRegisterStations() {
		// given
		SectionRequest request = createSectionRequest(구디역, 판교역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 구간을 제거한다.")
	@Test
	void removeSection() {
		// given
		지하철_구간_등록_요청_및_성공_체크(line_신분당선.getId(), createSectionRequest(역삼역, 구디역, 5));

		// when
		ExtractableResponse<Response> response = 지하철_구간_제거_요청(line_신분당선.getId(), 역삼역.getId());

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 첫번째 구간을 제거한다.")
	@Test
	void removeFirstSection() {
		// given
		지하철_구간_등록_요청_및_성공_체크(line_신분당선.getId(), createSectionRequest(역삼역, 구디역, 5));

		// when
		ExtractableResponse<Response> response = 지하철_구간_제거_요청(line_신분당선.getId(), 강남역.getId());

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 마지막 구간을 제거한다.")
	@Test
	void removeSectionLast() {
		// given
		지하철_구간_등록_요청_및_성공_체크(line_신분당선.getId(), createSectionRequest(역삼역, 구디역, 5));

		// when
		ExtractableResponse<Response> response = 지하철_구간_제거_요청(line_신분당선.getId(), 구디역.getId());

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("구간에 등록되지 않은 역을 제거한다.")
	@Test
	void removeSectionWithoutRegister() {
		// when
		ExtractableResponse<Response> response = 지하철_구간_제거_요청(line_신분당선.getId(), 판교역.getId());

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("하나 남은 구간을 제거한다.")
	@Test
	void removeLastSection() {
		// when
		ExtractableResponse<Response> response = 지하철_구간_제거_요청(line_신분당선.getId(), 강남역.getId());

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	public static ExtractableResponse<Response> 지하철_구간_등록_요청(final long lineId, final SectionRequest params) {
		// when
		String url = String.format("%s/%d%s", BASE_LINE_URL, lineId, BASE_SECTION_URL);
		return post(url, params);
	}

	public static LineResponse 지하철_구간_등록_요청_및_성공_체크(final long lineId, final SectionRequest params) {
		String url = String.format("%s/%d%s", BASE_LINE_URL, lineId, BASE_SECTION_URL);
		ExtractableResponse response = post(url, params);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		return response.as(LineResponse.class);
	}

	public static ExtractableResponse<Response> 지하철_구간_제거_요청(final long lineId, final long stationId) {
		// when
		String url = String.format("%s/%d%s", BASE_LINE_URL, lineId, BASE_SECTION_URL);

		Map<String, Object> params = new HashMap<>();
		params.put("stationId", stationId);
		return deleteWithParams(url, params);
	}
}
