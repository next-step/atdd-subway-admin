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
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.config.BaseAcceptanceTest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.SectionCreateRequest;

@DisplayName("지하철 구간 삭제 기능")
class SectionRemoveAcceptanceTest extends BaseAcceptanceTest {

	private Long 이호선;
	private Long 강남역;
	private Long 역삼역;
	private Long 선릉역;

	@BeforeEach
	public void setUp() {
		super.setUp();
		지하철_노선_추가();
		지하철_구간_추가();
	}

	private void 지하철_구간_추가() {
		역삼역 = id(지하철역_생성_요청("역삼역"));
		SectionCreateRequest request = new SectionCreateRequest(강남역, 역삼역, 100);
		지하철_구간_등록_요청(이호선, request);
	}

	private void 지하철_노선_추가() {
		LineResponse lineResponse = 지하철_노선_생성_요청("이호선", "bg-green-600", "강남역", "선릉역", 200).as(LineResponse.class);
		강남역 = lineResponse.getStations().get(0).getId();
		선릉역 = lineResponse.getStations().get(1).getId();
		이호선 = lineResponse.getId();
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역"-"역삼역"-"선릉역" 구간이 등록되어 있다.
	 * And 각 구간의 거리는 100 이다.
	 * When 노선에서 "강남역" 을 삭제한다.
	 * Then 노선 조회 시, "역삼역" - "선릉역" 구간을 확인할 수 있다.
	 * And 노선 조회 시, "역삼역" - "선릉역" 구간의 거리는 100 이다.
	 */
	@DisplayName("지하철 노선에서 상행 종점역을 삭제한다.")
	@Test
	void removeFirstUpStationTest() {
		// when
		ExtractableResponse<Response> deleteResponse = 지하철_구간_삭제_요청(이호선, 강남역);
		ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청(이호선);

		// then
		assertAll(
			() -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
			() -> assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(findResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(역삼역, 선릉역)
		);
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역"-"역삼역"-"선릉역" 구간이 등록되어 있다.
	 * And 각 구간의 거리는 100 이다.
	 * When 노선에서 "선릉역" 을 삭제한다.
	 * Then 노선 조회 시, "강남역" - "역삼역" 구간을 확인할 수 있다.
	 */
	@DisplayName("지하철 노선에서 하행 종점역을 삭제한다.")
	@Test
	void removeLastDownStationTest() {
		// when
		지하철_구간_삭제_요청(이호선, 선릉역);
		ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청(이호선);

		// then
		assertThat(findResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 역삼역);
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역"-"역삼역"-"선릉역" 구간이 등록되어 있다.
	 * And 각 구간의 거리는 100 이다.
	 * When 노선에서 "역삼역" 을 삭제한다.
	 * Then 노선 조회 시, "강남역" - "선릉역" 구간을 확인할 수 있다.
	 * And 노선 조회 시, "강남역" - "선릉역" 구간의 거리는 200 이다.
	 */
	@DisplayName("지하철 노선에서 중간역을 삭제한다.")
	@Test
	void removeMiddleStationTest() {
		// when
		지하철_구간_삭제_요청(이호선, 역삼역);
		ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청(이호선);

		// then
		assertThat(findResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 선릉역);
	}

	/**
	 * Given 지하철 노선 "2호선"에 "강남역"-"역삼역"-"선릉역" 구간이 등록되어 있다.
	 * When 노선에서 "잠실역" 을 삭제한다.
	 * Then 삭제할 수 없다.
	 */
	@DisplayName("지하철 노선에서 존재하지 않는 역을 삭제한다.")
	@Test
	void removeStationNotExistsTest() {
		// when
		Long 잠실역 = id(지하철역_생성_요청("잠실역"));
		ExtractableResponse<Response> deleteResponse = 지하철_구간_삭제_요청(이호선, 잠실역);

		// then
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	/**
	 * Given 지하철 노선 "분당선"에 "양재역"-"판교역" 구간이 등록되어 있다.
	 * When 노선에서 "양재역" 혹은 "판교역" 을 삭제한다.
	 * Then 삭제할 수 없다.
	 */
	@DisplayName("지하철 노선에서 구간이 하나인 역을 삭제한다.")
	@Test
	void removeStationHasOneSectionTest() {
		// given
		LineResponse lineResponse = 지하철_노선_생성_요청("분당선", "bg-red-600", "양재역", "판교역").as(LineResponse.class);
		Long 양재역 = lineResponse.getStations().get(0).getId();
		Long 판교역 = lineResponse.getStations().get(1).getId();
		Long 분당선 = lineResponse.getId();

		// when
		ExtractableResponse<Response> deleteResponseFromRemoveUpStation = 지하철_구간_삭제_요청(분당선, 양재역);
		ExtractableResponse<Response> deleteResponseFromRemoveDownStation = 지하철_구간_삭제_요청(분당선, 판교역);

		// then
		assertThat(deleteResponseFromRemoveUpStation.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		assertThat(deleteResponseFromRemoveDownStation.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

}
