package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private String 이수역_아이디;
	private String 고속터미널역_아이디;
	private String 칠호선_url;
	private String 칠호선_아이디;

	private String 이수역_고속터미널역_간격;

	public static String 노선_아이디_추출(String 노선_url) {
		return 노선_url.split("/")[2];
	}

	private static ExtractableResponse<Response> 구간_생성_요청(String 노선_아이디, SectionRequest 구간정보) {
		return RestAssured.given().log().all()
			.body(구간정보)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + 노선_아이디 + "/sections")
			.then().log().all()
			.extract();
	}

	@BeforeEach
	void 초기화() {
		고속터미널역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("고속터미널역"));
		이수역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("이수역"));

		이수역_고속터미널역_간격 = "100";

		칠호선_url = LineAcceptanceTest.노선_등록되어_있음(
			new LineRequest("7호선", "#777777", 이수역_아이디, 고속터미널역_아이디, 이수역_고속터미널역_간격));
		칠호선_아이디 = 노선_아이디_추출(칠호선_url);
	}

	@DisplayName("지하철 노선 내 역 사이에 상행역 기준으로 새로운 역을 등록한다.")
	@Test
	void addSectionBaseOnUpStation() {
		// given
		String 중간역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("중간역"));
		int 이수역_중간역_간격 = 50;
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(이수역_아이디), Long.parseLong(중간역_아이디), 이수역_중간역_간격);

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성된다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(이수역_아이디, 중간역_아이디, 고속터미널역_아이디));
	}

	@DisplayName("지하철 노선 내 역 사이에 하행역 기준으로 새로운 역을 등록한다.")
	@Test
	void addSectionBaseOnDownStation() {
		// given
		String 중간역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("중간역"));
		int 중간역_고속터미널역_간격 = 50;
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(중간역_아이디), Long.parseLong(고속터미널역_아이디), 중간역_고속터미널역_간격);

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성된다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(이수역_아이디, 중간역_아이디, 고속터미널역_아이디));
	}

	@DisplayName("지하철 노선 내 새로운 역을 상행 종점으로 등록한다.")
	@Test
	void addSectionFirstStation() {
		// given
		String 상행종점역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("상행종점역"));
		int 상행종점역_이수역_간격 = 40;
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(상행종점역_아이디), Long.parseLong(이수역_아이디), 상행종점역_이수역_간격);

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성된다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(상행종점역_아이디, 이수역_아이디, 고속터미널역_아이디));
	}

	@DisplayName("지하철 노선 내 새로운 역을 하행 종점으로 등록한다.")
	@Test
	void addSectionLastStation() {
		// given
		String 하행종점역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("하행종점역"));
		int 고속터미널역_하행종점역_간격 = 40;
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(고속터미널역_아이디), Long.parseLong(하행종점역_아이디),
			고속터미널역_하행종점역_간격);

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성된다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(이수역_아이디, 고속터미널역_아이디, 하행종점역_아이디));
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
	@Test
	void addSectionOverDistance() {
		// given
		String 중간역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("중간역"));
		int 이수역_중간역_간격 = 100;
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(이수역_아이디), Long.parseLong(중간역_아이디), 이수역_중간역_간격);

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성_실패한다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(이수역_아이디, 고속터미널역_아이디));
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
	@Test
	void addSectionAlreadyRegistedUpStationAndDownStation() {
		// given
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(고속터미널역_아이디), Long.parseLong(이수역_아이디),
			Integer.parseInt(이수역_고속터미널역_간격));

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성_실패한다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(이수역_아이디, 고속터미널역_아이디));
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
	@Test
	void addSectionDoNotRegistedUpStationAndDownStation() {
		// given
		String 상행종점역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("상행종점역"));
		String 하행종점역_아이디 = StationAcceptanceTest.지하철_역_등록되어_있음_아이디_응답(new StationRequest("하행종점역"));
		int 상행종점역_하행종점역_간격 = 40;
		SectionRequest 구간정보 = new SectionRequest(Long.parseLong(상행종점역_아이디), Long.parseLong(하행종점역_아이디),
			상행종점역_하행종점역_간격);

		// when
		ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(칠호선_아이디, 구간정보);

		// then
		구간이_생성_실패한다(구간_생성_응답);
		노선에_역정보들_포함_및_순서가_일치한다(칠호선_url, Arrays.asList(이수역_아이디, 고속터미널역_아이디));
	}

	private void 구간이_생성된다(ExtractableResponse<Response> 구간_생성_응답) {
		assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 노선에_역정보들_포함_및_순서가_일치한다(String 노선_url, List<String> 역_아이디들) {
		ExtractableResponse<Response> 노선_조회_응답 = LineAcceptanceTest.노선_조회_요청(노선_url);
		List<Long> 응답된_역_아이디들 = 노선_조회_응답.jsonPath().getList("stations", StationResponse.class)
			.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		List<Long> 예상_역_아이디들 = 역_아이디들.stream()
			.map(Long::parseLong)
			.collect(Collectors.toList());
		assertThat(응답된_역_아이디들).containsAll(예상_역_아이디들);
	}

	private void 구간이_생성_실패한다(ExtractableResponse<Response> 구간_생성_응답) {
		assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
