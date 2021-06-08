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

import static nextstep.subway.line.SectionAcceptanceTestUtils.createSectionRequest;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성_요청_및_성공_체크;
import static nextstep.subway.line.LineAcceptanceTestUtils.createLineRequest;
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


	@DisplayName("지하철 노선 구간을 등록한다.")
	@Test
	void registSection() {
		// given
		SectionRequest request = createSectionRequest(역삼역, 구디역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(line_신분당선.getId(), request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("기존 역 사이 길이보다 큰 새로운 역을 등록한다.")
	@Test
	void registSectionWithBiggerThanOriginalDistance() {
		// given
		SectionRequest request = createSectionRequest(역삼역, 구디역, 20);

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

	public static ExtractableResponse<Response> 지하철_구간_등록_요청(final long lineId, final SectionRequest params) {
		// when
		return post(String.format("%s/%d/%s", BASE_LINE_URL, lineId, BASE_SECTION_URL), params);
	}
}
