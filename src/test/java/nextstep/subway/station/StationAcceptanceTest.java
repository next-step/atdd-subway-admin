package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	private Map<String, String> 강남역_파라메터;
	private Map<String, String> 역삼역_파라메터;

	@BeforeEach
	void 초기화() {
		강남역_파라메터 = new HashMap<>();
		강남역_파라메터.put("name", "강남역");

		역삼역_파라메터 = new HashMap<>();
		역삼역_파라메터.put("name", "역삼역");
	}

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// given

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(강남역_파라메터);

		// then
		지하철역이_생성된다(생성_응답);
	}

	@DisplayName("지하철 역 이름을 전달하지 않고 생성한다.")
	@Test
	void createStationWithNullName() {
		// given
		Map<String, String> 역이름_없는_파라메터 = new HashMap<>();

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역이름_없는_파라메터);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 역 이름을 공백(\"\")으로 생성한다.")
	@Test
	void createStationWithBlankName() {
		// given
		Map<String, String> 공백_역이름_파라메터 = new HashMap<>();
		공백_역이름_파라메터.put("name", "");

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(공백_역이름_파라메터);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 역 이름을 상당히 길게 작성하여 생성한다.")
	@Test
	void createStationWithLongName() {
		// given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		Map<String, String> 긴_역이름_파라메터 = new HashMap<>();
		긴_역이름_파라메터.put("name", 이백오십육바이트_이름);

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(긴_역이름_파라메터);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		지하철역_생성_요청(강남역_파라메터);

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(강남역_파라메터);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철역 목록을 조회한다.")
	@Test
	void getStations() {
		/// given
		ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);
		ExtractableResponse<Response> 역삼역_생성_응답 = 지하철역_생성_요청(역삼역_파라메터);

		// when
		ExtractableResponse<Response> 목록_조회_응답 = 지하철역_목록_조회_요청();

		// then
		지하철역이_응답된다(목록_조회_응답);
		지하철역이_포함되어_있다(강남역_생성_응답, 역삼역_생성_응답, 목록_조회_응답);
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStation() {
		/// given
		ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);

		// when
		ExtractableResponse<Response> 조회_응답 = 지하철역_조회_요청(지하철역_생성_응답);

		// then
		지하철역이_응답된다(조회_응답);
		지하철역이_동일하다(지하철역_생성_응답, 조회_응답);
	}

	@DisplayName("지하철역을 수정한다.")
	@Test
	void updateStation() {
		// given
		ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(지하철역_생성_응답, 역삼역_파라메터);

		// then
		지하철역이_수정된다(지하철역_수정_응답);
	}

	@DisplayName("지하철 역 이름을 전달하지 않고 수정한다.")
	@Test
	void updateStationWithNullName() {
		// given
		ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);
		Map<String, String> 역이름_없는_파라메터 = new HashMap<>();

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(지하철역_생성_응답, 역이름_없는_파라메터);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철 역 이름을 공백(\"\")으로 수정한다.")
	@Test
	void updateStationWithBlankName() {
		// given
		ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);
		Map<String, String> 공백_역이름_파라메터 = new HashMap<>();
		공백_역이름_파라메터.put("name", "");

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(지하철역_생성_응답, 공백_역이름_파라메터);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철 역 이름을 상당히 길게 작성하여 수정한다.")
	@Test
	void updateStationWithLongName() {
		// given
		ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		Map<String, String> 긴_역이름_파라메터 = new HashMap<>();
		긴_역이름_파라메터.put("name", 이백오십육바이트_이름);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(지하철역_생성_응답, 긴_역이름_파라메터);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정한다.")
	@Test
	void updateStationWithDuplicateName() {
		// given
		ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);
		ExtractableResponse<Response> 역삼역_생성_응답 = 지하철역_생성_요청(역삼역_파라메터);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(역삼역_생성_응답, 강남역_파라메터);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역_파라메터);

		// when
		ExtractableResponse<Response> 지하철역_삭제_응답 = 지하철역_삭제_요청(지하철역_생성_응답);

		// then
		지하철역이_삭제된다(지하철역_삭제_응답);
	}

	private ExtractableResponse<Response> 지하철역_생성_요청(Map<String, String> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}

	private void 지하철역이_생성된다(ExtractableResponse<Response> 생성_응답) {
		assertThat(생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(생성_응답.header("Location")).isNotBlank();
	}

	private void 지하철역이_생성_실패된다(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private void 지하철역이_응답된다(ExtractableResponse<Response> 목록_조회_응답) {
		assertThat(목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철역이_포함되어_있다(ExtractableResponse<Response> 강남역_생성_응답,
		ExtractableResponse<Response> 역삼역_생성_응답,
		ExtractableResponse<Response> 목록_조회_응답) {
		List<Long> 예상_지하철역_아이디들 = 생성_응답에서_지하철역_아이디_추출하기(강남역_생성_응답, 역삼역_생성_응답);
		List<Long> 비교할_지하철역_아이디들 = 목록_조회_응답에서_지하철역_아이디_추출하기(목록_조회_응답);
		assertThat(비교할_지하철역_아이디들).containsAll(예상_지하철역_아이디들);
	}

	private List<Long> 목록_조회_응답에서_지하철역_아이디_추출하기(ExtractableResponse<Response> 목록_조회_응답) {
		List<Long> resultLineIds = 목록_조회_응답.jsonPath().getList(".", StationResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		return resultLineIds;
	}

	private List<Long> 생성_응답에서_지하철역_아이디_추출하기(ExtractableResponse<Response> 강남역_생성_응답,
		ExtractableResponse<Response> 역삼역_생성_응답) {
		List<Long> expectedLineIds = Arrays.asList(강남역_생성_응답, 역삼역_생성_응답).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		return expectedLineIds;
	}

	private ExtractableResponse<Response> 지하철역_목록_조회_요청() {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();
		return response;
	}

	private ExtractableResponse<Response> 지하철역_조회_요청(ExtractableResponse<Response> 지하철역_생성_응답) {
		String uri = 지하철역_생성_응답.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get(uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 지하철역이_동일하다(ExtractableResponse<Response> 지하철역_생성_응답, ExtractableResponse<Response> 조회_응답) {
		StationResponse 생성시_응답 = 지하철역_생성_응답.jsonPath().getObject(".", StationResponse.class);
		StationResponse 조회후_응답 = 조회_응답.jsonPath().getObject(".", StationResponse.class);
		assertThat(생성시_응답.equals(조회후_응답)).isTrue();
	}

	private ExtractableResponse<Response> 지하철역_수정_요청(ExtractableResponse<Response> 지하철역_생성_응답,
		Map<String, String> 파라메터) {
		String uri = 지하철역_생성_응답.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(파라메터)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put(uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 지하철역이_수정된다(ExtractableResponse<Response> 지하철역_수정_응답) {
		assertThat(지하철역_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철역이_수정이_실패된다(ExtractableResponse<Response> 지하철역_수정_응답) {
		assertThat(지하철역_수정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철역_삭제_요청(ExtractableResponse<Response> 지하철역_생성_응답) {
		String uri = 지하철역_생성_응답.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 지하철역이_삭제된다(ExtractableResponse<Response> 지하철역_삭제_응답) {
		assertThat(지하철역_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
