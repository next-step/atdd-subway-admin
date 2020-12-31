package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private long 노선;
	private long A역;
	private long 추가역;
	private long C역;
	private long 존재하지않는역;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
		A역 = SectionAcceptanceTestSupport.지하철역_생성_요청("A");
		추가역 = SectionAcceptanceTestSupport.지하철역_생성_요청("추가역");
		C역 = SectionAcceptanceTestSupport.지하철역_생성_요청("C");
		존재하지않는역 = 99;
		노선 = SectionAcceptanceTestSupport.지하철노선_생성_요청("노선", "green", A역, C역, 50);
	}

	@Test
	@DisplayName("역 사이에 구간을 추가할 경우")
	void addSection_역사이() {
		// when
		SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, 추가역, C역, 30);

		// then
		SectionAcceptanceTestSupport.지하철노선_구간_검사(노선, A역, 추가역, C역);
	}

	@Test
	@DisplayName("새로운 역을 상행 종점으로 등록할 경우")
	void addSection_상행종점() {
		// when
		SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, 추가역, A역, 30);

		// then
		SectionAcceptanceTestSupport.지하철노선_구간_검사(노선, 추가역, A역, C역);
	}

	@Test
	@DisplayName("새로운 역을 하행 종점으로 등록할 경우")
	void addSection_하행종점() {
		// when
		SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, C역, 추가역, 30);

		// then
		SectionAcceptanceTestSupport.지하철노선_구간_검사(노선, A역, C역, 추가역);
	}

	@Test
	@DisplayName("예외 : 역 사이에 구간 추가시 distance 가 역 사이 길이보다 작지 않은 경우")
	void addSection_역사이_DistanceOver() {
		// when
		ExtractableResponse<Response> response =
				SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, 추가역, C역, 1000);

		// then
		SectionAcceptanceTestSupport.assertStatusCode(response, HttpStatus.BAD_REQUEST);
	}

	@Test
	@DisplayName("예외 : 구간 추가시 추가할 상행역과 하행역이 이미 노선에 등록되어 있는 경우")
	void addSection_이미추가됨() {
		// when
		ExtractableResponse<Response> response =
				SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, A역, C역, 1);

		// then
		SectionAcceptanceTestSupport.assertStatusCode(response, HttpStatus.BAD_REQUEST);
	}

	@Test
	@DisplayName("예외 : 구간 추가시 상행과 하행 둘 중 하나라도 존재하지 않는 경우")
	void addSection_존재하지않는역() {
		// when
		ExtractableResponse<Response> response1 =
				SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, A역, 존재하지않는역, 1);

		ExtractableResponse<Response> response2 =
				SectionAcceptanceTestSupport.지하철노선_구간_추가(노선, 존재하지않는역, A역, 1);

		// then
		SectionAcceptanceTestSupport.assertStatusCode(response1, HttpStatus.BAD_REQUEST);
		SectionAcceptanceTestSupport.assertStatusCode(response2, HttpStatus.BAD_REQUEST);
	}
}
