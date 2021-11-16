package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.testFactory.AcceptanceTestFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	private static final String STATION_SERVICE_PATH = "/stations";

	//	given
	public static final Map<String, String> 강남역_정보 = 지하철역_이름_정의("강남역");
	public static final Map<String, String> 역삼역_정보 = 지하철역_이름_정의("역삼역");

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> 강남역_생성_결과 = 지하철역_생성(강남역_정보);

		// then
		정상_생성_확인(강남역_생성_결과);
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		지하철역_생성(강남역_정보);

		// when
		ExtractableResponse<Response> 강남역_중복_생성_결과 = 지하철역_생성(강남역_정보);

		// then
		예외_발생_확인(강남역_중복_생성_결과);
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		// given
		ExtractableResponse<Response> 강남역_생성_되어있음 = 지하철역_생성(강남역_정보);
		ExtractableResponse<Response> 역삼역_생성_되어있음 = 지하철역_생성(역삼역_정보);

		// when
		ExtractableResponse<Response> 지하철역_목록_결과 = 지하철역_조회(STATION_SERVICE_PATH);

		// then
		정상_처리_확인(지하철역_목록_결과);
		생성된_지하철역이_응답_목록에_포함되어있는_확인(Arrays.asList(강남역_생성_되어있음, 역삼역_생성_되어있음), 지하철역_목록_결과);
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		ExtractableResponse<Response> 강남역_생성_되어있음 = 지하철역_생성(강남역_정보);
		String 강남역_요청_경로 = 강남역_생성_되어있음.header("Location");

		// when
		ExtractableResponse<Response> 강남역_삭제_결과 = 지하철역_삭제(강남역_요청_경로);

		// then
		삭제_완료_확인(강남역_삭제_결과);
	}

	public static Map<String, String> 지하철역_이름_정의(String name) {
		return AcceptanceTestFactory.getNameContent(name);
	}

	public static ExtractableResponse<Response> 지하철역_생성(Map<String, String> params) {
		return AcceptanceTestFactory.post(params, STATION_SERVICE_PATH);
	}

	private void 정상_생성_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	private void 예외_발생_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철역_조회(String path) {
		return AcceptanceTestFactory.get(path);
	}

	private void 생성된_지하철역이_응답_목록에_포함되어있는_확인(List<ExtractableResponse<Response>> registeredStationResponses,
		ExtractableResponse<Response> response) {
		List<Long> expectedLineIds = registeredStationResponses.stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	private void 정상_처리_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철역_삭제(String createdUri) {
		return AcceptanceTestFactory.delete(createdUri);
	}

	private void 삭제_완료_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
