package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {

	private long 노선;
	private long A역;
	private long 추가역;
	private long C역;
	private long 노선미포함역;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
		A역 = SectionAcceptanceTestSupport.지하철역_생성_요청("A");
		추가역 = SectionAcceptanceTestSupport.지하철역_생성_요청("추가역");
		C역 = SectionAcceptanceTestSupport.지하철역_생성_요청("C");
		노선미포함역 = SectionAcceptanceTestSupport.지하철역_생성_요청("노선미포함역");
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

	// TODO : 예외 : 역 사이에 새로운 역을 등록할 경우 역 사이 길이보다 크거나 같으면 등록 못함

	// TODO : 예외 : 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음

	// TODO : 예외 : 상행과 하행 둘 중 하나도 포함되어 있지 않은 경우
}
