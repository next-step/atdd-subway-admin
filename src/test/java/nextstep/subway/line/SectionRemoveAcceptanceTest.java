package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.line.SectionAddAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 삭제 관련 기능")
public class SectionRemoveAcceptanceTest extends AcceptanceTest {

	/**
	 * [AS-IS ] (강남역)--4m--(역삼역)-3m--(선릉역)
	 * [REMOVE] (강남역)
	 * [TO-BE ] (역삼역)-3m--(선릉역)
	 */
	@DisplayName("지하철 구간을 삭제한다. (세 역 중 첫번째 역이 제거될 경우)")
	@Test
	void removeSectionByFirstStation() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 3));
		지하철_구간_등록되어_있음(노선_2호선_생성_응답.getId(), SectionFixture.구간_등록_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 4));

		// when
		ExtractableResponse<Response> 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(노선_2호선_생성_응답.getId(), 강남역_생성_응답.getId());

		// then
		지하철_구간_삭제됨(지하철_구간_삭제_응답);
		지하철_노선에_지하철_역이_주어진_순서대로_조회됨(노선_2호선_생성_응답.getId(), Arrays.asList(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId()));
	}

	/**
	 * [AS-IS ] (강남역)--4m--(역삼역)-3m--(선릉역)
	 * [REMOVE] (역삼역)
	 * [TO-BE ] (강남역)---7m----(선릉역)
	 */
	@DisplayName("지하철 구간을 삭제한다. (세 역 중 두번째 역이 제거될 경우)")
	@Test
	void removeSectionBySecondStation() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 3));
		지하철_구간_등록되어_있음(노선_2호선_생성_응답.getId(), SectionFixture.구간_등록_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 4));

		// when
		ExtractableResponse<Response> 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(노선_2호선_생성_응답.getId(), 역삼역_생성_응답.getId());

		// then
		지하철_구간_삭제됨(지하철_구간_삭제_응답);
		지하철_노선에_지하철_역이_주어진_순서대로_조회됨(노선_2호선_생성_응답.getId(), Arrays.asList(강남역_생성_응답.getId(), 선릉역_생성_응답.getId()));
	}

	/**
	 * [AS-IS ] (강남역)--4m--(역삼역)-3m--(선릉역)
	 * [REMOVE] (선릉역)
	 * [TO-BE ] (강남역)--4m--(역삼역)
	 */
	@DisplayName("지하철 구간을 삭제한다. (세 역 중 세번째 역이 제거될 경우)")
	@Test
	void removeSectionByThirdStation() {
		// given
		StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
		StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
		StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

		LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 3));
		지하철_구간_등록되어_있음(노선_2호선_생성_응답.getId(), SectionFixture.구간_등록_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 4));

		// when
		ExtractableResponse<Response> 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(노선_2호선_생성_응답.getId(), 선릉역_생성_응답.getId());

		// then
		지하철_구간_삭제됨(지하철_구간_삭제_응답);
		지하철_노선에_지하철_역이_주어진_순서대로_조회됨(노선_2호선_생성_응답.getId(), Arrays.asList(강남역_생성_응답.getId(), 역삼역_생성_응답.getId()));
	}

	private ExtractableResponse<Response> 지하철_구간_삭제_요청(Long lineId, Long stationId) {
		return RestAssured.given().log().all()
			.queryParam("stationId", stationId)
			.when()
			.delete("/lines/{lineId}/sections", lineId)
			.then().log().all()
			.extract();
	}

	private void 지하철_구간_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
