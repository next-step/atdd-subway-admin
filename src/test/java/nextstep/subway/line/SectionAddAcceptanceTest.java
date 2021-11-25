package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 추가 관련 기능")
public class SectionAddAcceptanceTest extends AcceptanceTest {

	/**
	 * [AS-IS] (강남역)---7m----(선릉역)
	 * [ ADD ] (강남역)--4m--(역삼역)
	 * [TO-BE] (강남역)--4m--(역삼역)-3m--(선릉역)
	 */
	@DisplayName("지하철 구간을 등록한다. (역 사이에 새로운 역을 등록할 경우)")
	@Test
	void addSectionBetweenStations() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 4);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록됨(지하철_구간_등록_응답);
		지하철_노선에_지하철_역이_주어진_순서대로_조회됨(노선_2호선_생성_응답.getId(), Arrays.asList(
			강남역_생성_응답.getId(),
			역삼역_생성_응답.getId(),
			선릉역_생성_응답.getId()));
	}

	/**
	 * [AS-IS]             (역삼역)---7m----(선릉역)
	 * [ ADD ] (강남역)--4m--(역삼역)
	 * [TO-BE] (강남역)--4m--(역삼역)---7m----(선릉역)
	 */
	@DisplayName("지하철 구간을 등록한다. (새로운 역을 상행 종점으로 등록할 경우)")
	@Test
	void addSectionToUpStation() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 4);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록됨(지하철_구간_등록_응답);
		지하철_노선에_지하철_역이_주어진_순서대로_조회됨(노선_2호선_생성_응답.getId(), Arrays.asList(
			강남역_생성_응답.getId(),
			역삼역_생성_응답.getId(),
			선릉역_생성_응답.getId()));
	}

	/**
	 * [AS-IS] (강남역)---7m----(역삼역)
	 * [ ADD ]                (역삼역)-3m--(선릉역)
	 * [TO-BE] (강남역)---7m----(역삼역)-3m--(선릉역)
	 *
	 */
	@DisplayName("지하철 구간을 등록한다. (새로운 역을 하행 종점으로 등록할 경우)")
	@Test
	void addSectionToDownStation() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 7);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 3);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록됨(지하철_구간_등록_응답);
		지하철_노선에_지하철_역이_주어진_순서대로_조회됨(노선_2호선_생성_응답.getId(), Arrays.asList(
			강남역_생성_응답.getId(),
			역삼역_생성_응답.getId(),
			선릉역_생성_응답.getId()));
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않을 때, 구간을 등록할 수 없다.")
	@Test
	void addSectionFailOnBothStationsNotRegistered() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());
		StationResponse 삼성역_생성_응답 = 지하철_역_등록되어_있음(삼성역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 1);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(선릉역_생성_응답.getId(), 삼성역_생성_응답.getId(), 2);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록_실패됨(지하철_구간_등록_응답);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않을 때, 구간을 등록할 수 없다.")
	@Test
	void addSectionFailOnBothStationsAlreadyRegistered() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 1);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 2);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록_실패됨(지하철_구간_등록_응답);
	}

	@DisplayName("역 사이 왼쪽에 새로운 구간을 등록할 때, 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.")
	@Test
	void addSectionBetweenStationsAtLeftSideFailOnIllegalDistance() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 7);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록_실패됨(지하철_구간_등록_응답);
	}

	@DisplayName("역 사이 오른쪽에 새로운 구간을 등록할 때, 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.")
	@Test
	void addSectionBetweenStationsAtRightSideFailOnIllegalDistance() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);
		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

		SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);

		// when
		ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

		// then
		지하철_구간_등록_실패됨(지하철_구간_등록_응답);
	}

	private static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, SectionAddRequest request) {
		return RestAssured.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/{lineId}/sections", lineId)
			.then().log().all()
			.extract();
	}

	private void 지하철_구간_등록됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선에_지하철_역이_주어진_순서대로_조회됨(Long lineId, List<Long> expectedStationIds) {
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
		LineResponse lineResponse = response.as(LineResponse.class);
		List<Long> actualStationIds = lineResponse.getStations()
			.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		assertThat(actualStationIds).isEqualTo(expectedStationIds);
	}

	private void 지하철_구간_등록_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	public static void 지하철_구간_등록되어_있음(Long lineId, SectionAddRequest request) {
		지하철_구간_등록_요청(lineId, request);
	}
}
