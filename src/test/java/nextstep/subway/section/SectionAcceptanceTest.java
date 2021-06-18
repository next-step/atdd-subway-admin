package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

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
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("구간 인수테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

	private static StationResponse 행신역;
	private static StationResponse 서울역;
	private static StationResponse 광명역;
	private static StationResponse 대전역;
	private static StationResponse 동대구역;

	private static LineResponse 경부고속선;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
		행신역 = StationAcceptanceTest.지하철역_등록되어_있음("행신역");
		서울역 = StationAcceptanceTest.지하철역_등록되어_있음("서울역");
		광명역 = StationAcceptanceTest.지하철역_등록되어_있음("광명역");
		대전역 = StationAcceptanceTest.지하철역_등록되어_있음("대전역");
		동대구역 = StationAcceptanceTest.지하철역_등록되어_있음("동대구역");

		경부고속선 = LineAcceptanceTest.지하철_노선_등록되어_있음("경부고속선", "BLUE", 서울역.getId(), 대전역.getId(), 10);
	}

	@DisplayName("구간 사이에 새로운 구간을 추가한다.(시작역이 같은 경우)")
	@Test
	void addSection1() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(서울역.getId(), 광명역.getId(), 5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 값 검증
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations()).extracting("name").containsExactly("서울역", "광명역", "대전역");
	}

	@DisplayName("구간 사이에 새로운 구간을 추가한다.(종료역이 같은 경우)")
	@Test
	void addSection2() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(광명역.getId(), 대전역.getId(), 5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 값 검증
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations()).extracting("name").containsExactly("서울역", "광명역", "대전역");
	}

	@DisplayName("새로운 구간을 상행에 추가한다.")
	@Test
	void addSection3() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(행신역.getId(), 서울역.getId(), 5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 값 검증
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations()).extracting("name").containsExactly("행신역", "서울역", "대전역");
	}

	@DisplayName("새로운 구간을 하행에 추가한다.")
	@Test
	void addSection4() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(대전역.getId(), 동대구역.getId(), 5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 값 검증
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getStations()).extracting("name").containsExactly("서울역", "대전역", "동대구역");
	}

	@DisplayName("시작, 종료역이 같은 구간을 추가한다.")
	@Test
	void addSection5() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(서울역.getId(), 대전역.getId(), 5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
	}

	@DisplayName("시작, 종료역이 하나도 같지 않은 구간을 추가한다.")
	@Test
	void addSection6() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(광명역.getId(), 동대구역.getId(), 5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
	}

	@DisplayName("기존구간보다 긴 구간을 추가한다.")
	@Test
	void addSection7() {
		ExtractableResponse<Response> response = 경부고속선_노선에_구간_등록_요청(서울역.getId(), 동대구역.getId(), 15);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
	}

	private ExtractableResponse<Response> 경부고속선_노선에_구간_등록_요청(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId.toString());
		params.put("downStationId", downStationId.toString());
		params.put("distance", Integer.toString(distance));

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/{lineId}/sections", 경부고속선.getId())
			.then().log().all()
			.extract();
	}
}
