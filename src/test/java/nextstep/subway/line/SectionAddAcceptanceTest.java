package nextstep.subway.line;

import static nextstep.subway.util.LineAcceptanceUtils.*;
import static nextstep.subway.util.ResponseExtractUtils.*;
import static nextstep.subway.util.SectionAcceptanceUtils.*;
import static nextstep.subway.util.StationAcceptanceUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.config.BaseAcceptanceTest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.SectionCreateRequest;

@DisplayName("지하철 구간 추가 기능")
class SectionAddAcceptanceTest extends BaseAcceptanceTest {

	private Long 이호선;
	private Long 강남역;
	private Long 선릉역;

	@BeforeEach
	public void setUp() {
		super.setUp();
		LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "bg-green-600", "강남역", "선릉역")
			.as(LineResponse.class);
		강남역 = lineResponse.getStations().get(0).getId();
		선릉역 = lineResponse.getStations().get(1).getId();
		이호선 = lineResponse.getId();
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역", "선릉역" 이 등록되어 있다.
	 * And 지하철역 "역삼역"이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "역삼역"과 "선릉역" 구간을 추가한다.
	 * Then 노선 조회 시, 추가한 구간("강남역" - "역삼역" - "선릉역")을 확인 할 수 있다.
	 */
	@DisplayName("지하철 노선에 구간을 추가한다. - 중간 역 추가(동일 상행역)")
	@Test
	void addSectionBetweenExistingStationsWithSameUpStation() {
		// given
		Long 역삼역 = id(지하철역_생성_요청("역삼역"));
		SectionCreateRequest request = new SectionCreateRequest(강남역, 역삼역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, request);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 역삼역, 선릉역)
		);
	}

	/**
	 * Scenario: 지하철 노선에 구간을 등록한다.
	 * Given 지하철 노선 "2호선"에 "강남역", "선릉역" 이 등록되어 있다.
	 * And 지하철역 "역삼역"이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "역삼역"과 "선릉역" 구간을 추가한다.
	 * Then 노선 조회 시, 추가한 구간("강남역" - "역삼역" - "선릉역")을 확인 할 수 있다.
	 */
	@DisplayName("지하철 노선에 구간을 추가한다. - 중간 역 추가(동일 하행역)")
	@Test
	void addSectionBetweenExistingStationsWithSameDownStations() {
		// given
		Long 역삼역 = id(지하철역_생성_요청("역삼역"));
		SectionCreateRequest request = new SectionCreateRequest(역삼역, 선릉역, 5);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, request);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 역삼역, 선릉역)
		);
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역", "선릉역" 이 등록되어 있다.
	 * And 지하철역 "삼성역"이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "삼성역"과 "강남역" 구간을 추가한다.
	 * Then 노선 조회 시, 추가한 구간("삼성역" - "강남역" - "선릉역")을 확인 할 수 있다.
	 */
	@DisplayName("지하철 노선에 구간을 추가한다. - 상행 종점 추가")
	@Test
	void addSectionWithNewUpStation() {
		// given
		Long 삼성역 = id(지하철역_생성_요청("삼성역"));
		SectionCreateRequest request = new SectionCreateRequest(삼성역, 강남역, 10);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, request);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(삼성역, 강남역, 선릉역)
		);
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역", "선릉역" 이 등록되어 있다.
	 * And 지하철역 "잠실역"이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "선릉역"과 "잠실역" 구간을 추가한다.
	 * Then 노선 조회 시, 추가한 구간("강남역" - "선릉역" - "잠실역")을 확인 할 수 있다.
	 */
	@DisplayName("지하철 노선에 구간을 추가한다. - 하행 종점 추가")
	@Test
	void addSectionWithNewDownStation() {
		// given
		Long 잠실역 = id(지하철역_생성_요청("잠실역"));
		SectionCreateRequest request = new SectionCreateRequest(선릉역, 잠실역, 10);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, request);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 선릉역, 잠실역)
		);
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역", "선릉역" 구간이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "강남역"과 "선릉역" 구간을 추가한다.
	 * Then 등록할 수 없다.
	 */
	@DisplayName("추가 구간의 상행/하행역이 모두 노선에 존재하는 경우 등록할 수 없다.")
	@Test
	void addSectionWithBothStationExist() {
		// given
		SectionCreateRequest request = new SectionCreateRequest(강남역, 선릉역, 10);
		지하철_구간_등록_요청(이호선, request);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, new SectionCreateRequest(강남역, 선릉역, 5));

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역", "선릉역" 이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "삼성역"과 "잠실역" 구간을 추가한다.
	 * Then 등록할 수 없다.
	 */
	@DisplayName("추가 구간의 상행/하행역이 모두 노선에 존재하지 않는 경우 등록할 수 없다.")
	@Test
	void addSectionBothStationNotExist() {
		// given
		Long 삼성역 = id(지하철역_생성_요청("삼성역"));
		Long 잠실역 = id(지하철역_생성_요청("잠실역"));
		SectionCreateRequest request = new SectionCreateRequest(삼성역, 잠실역, 10);

		// when
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, request);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	/**
	 * Given 100 의 길이를 가진 지하철 노선 "2호선"에 "강남역", "선릉역" 이 등록되어 있다.
	 * And 50 의 길이를 가진 "강남역" - "역삼역" 구간이 등록되어 있다.
	 * When 지하철 노선 "2호선"에 "강남역"과 "잠실역" 구간을 추가한다.
	 *   When 0 의 길이를 가진 구간을 추가한다.
	 *   When 50 의 길이를 가진 구간을 추가한다.
	 *   When 100 의 길이를 가진 구간을 추가한다.
	 * hen 등록할 수 없다.
	 */
	@DisplayName("추가하려는 구간 사이의 거리가 기존 역 사이 거리보다 같거나 큰 경우 등록할 수 없다.")
	@ParameterizedTest(name = "{index}. 길이: {0}")
	@ValueSource(ints = {0, 50, 100})
	void addSectionWithDistanceGreaterThanExistSection(int distance) {
		// given
		SectionCreateRequest request = new SectionCreateRequest(강남역, 선릉역, 100);
		지하철_구간_등록_요청(이호선, request);
		Long 역삼역 = id(지하철역_생성_요청("역삼역"));
		SectionCreateRequest newRequest = new SectionCreateRequest(역삼역, 선릉역, 50);
		지하철_구간_등록_요청(이호선, newRequest);

		// when
		Long 잠실역 = id(지하철역_생성_요청("잠실역"));
		ExtractableResponse<Response> response = 지하철_구간_등록_요청(이호선, new SectionCreateRequest(강남역, 잠실역, distance));

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}
