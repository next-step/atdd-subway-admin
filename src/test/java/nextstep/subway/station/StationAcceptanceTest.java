package nextstep.subway.station;

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
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	private StationRequest 강남역;
	private StationRequest 역삼역;

	@BeforeEach
	void 초기화() {
		강남역 = new StationRequest("강남역");
		역삼역 = new StationRequest("역삼역");
	}

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// given

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(강남역);

		// then
		지하철역이_생성_응답된다(생성_응답);
		지하철역_정보가_동일하다(강남역, 생성_응답);
	}

	@DisplayName("지하철역을 생성하는 경우 지하철 역 이름을 전달하지 않으면 등록되지 않는다.")
	@Test
	void createStationWithNullName() {
		// given
		StationRequest 역이름_없음 = new StationRequest(null);

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역이름_없음);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철역을 생성하는 경우 지하철 역 이름을 공백(\"\")으로 전달하면 등록되지 않는다.")
	@Test
	void createStationWithBlankName() {
		// given
		StationRequest 역이름_공백 = new StationRequest("");

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역이름_공백);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철역을 생성하는 경우 지하철 역 이름을 상당히 길게 작성하면 등록되지 않는다.")
	@Test
	void createStationWithLongName() {
		// given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		StationRequest 역이름_너무_김 = new StationRequest(이백오십육바이트_이름);

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역이름_너무_김);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철역을 생성하는 경우 기존에 존재하는 지하철역 이름으로 전달하면 지하철역을 등록하지 못한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		지하철_역_등록되어_있음_uri_응답(강남역);

		// when
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(강남역);

		// then
		지하철역이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철역 목록을 조회한다.")
	@Test
	void getStations() {
		/// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);
		String 역삼역_응답 = 지하철_역_등록되어_있음_uri_응답(역삼역);

		// when
		ExtractableResponse<Response> 목록_조회_응답 = 지하철역_목록_조회_요청();

		// then
		지하철역이_응답된다(목록_조회_응답);
		지하철역이_포함되어_있다(목록_조회_응답, Arrays.asList(강남역_응답, 역삼역_응답));
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStation() {
		/// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);

		// when
		ExtractableResponse<Response> 조회_응답 = 지하철역_조회_요청(강남역_응답);

		// then
		지하철역이_응답된다(조회_응답);
		지하철역_정보가_동일하다(강남역, 조회_응답);
	}

	@DisplayName("지하철역을 조회할때 존재하지 않는 지하철역 아이디를 전달하면 조회할 수 없다.")
	@Test
	void getStationWithNotExistedId() {
		/// given
		String 존재하지_않는_역_uri = "/stations/" + Integer.MIN_VALUE;

		// when
		ExtractableResponse<Response> 조회_응답 = 지하철역_조회_요청(존재하지_않는_역_uri);

		// then
		지하철역이_응답이_실패된다(조회_응답);
	}

	@DisplayName("지하철역을 수정한다.")
	@Test
	void updateStation() {
		// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(강남역_응답, 역삼역);

		// then
		지하철역이_수정된다(지하철역_수정_응답);
	}

	@DisplayName("지하철역을 수정하는 경우 지하철 역 이름을 전달하지 않으면 수정할 수 없다.")
	@Test
	void updateStationWithNullName() {
		// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);
		StationRequest 역이름_없음 = new StationRequest(null);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(강남역_응답, 역이름_없음);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철역을 수정하는 경우 지하철 역 이름을 공백(\"\")으로 전달하면 수정할 수 없다.")
	@Test
	void updateStationWithBlankName() {
		// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);
		StationRequest 역이름_공백 = new StationRequest("");

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(강남역_응답, 역이름_공백);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철역을 수정하는 경우 지하철 역 이름을 상당히 길게 작성하면 수정할 수 없다.")
	@Test
	void updateStationWithLongName() {
		// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		StationRequest 역이름_너무_김 = new StationRequest(이백오십육바이트_이름);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(강남역_응답, 역이름_너무_김);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철역을 수정하는 경우 기존에 존재하는 지하철 노선 이름으로 전달하면 지하철 역을 수정할 수 없다.")
	@Test
	void updateStationWithDuplicateName() {
		// given
		지하철_역_등록되어_있음_uri_응답(강남역);
		String 역삼역_응답 = 지하철_역_등록되어_있음_uri_응답(역삼역);

		// when
		ExtractableResponse<Response> 지하철역_수정_응답 = 지하철역_수정_요청(역삼역_응답, 강남역);

		// then
		지하철역이_수정이_실패된다(지하철역_수정_응답);
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		String 강남역_응답 = 지하철_역_등록되어_있음_uri_응답(강남역);

		// when
		ExtractableResponse<Response> 지하철역_삭제_응답 = 지하철역_삭제_요청(강남역_응답);

		// then
		지하철역이_삭제된다(지하철역_삭제_응답);
	}

	public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}

	public static String 지하철_역_등록되어_있음_아이디_응답(StationRequest 역정보) {
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역정보);
		return 생성_응답.jsonPath().get("id").toString();
	}

	public static Long 지하철_역_등록되어_있음(StationRequest 역정보) {
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역정보);
		return 생성_응답.jsonPath().getLong("id");
	}

	private void 지하철역이_생성_응답된다(ExtractableResponse<Response> 생성_응답) {
		assertThat(생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(생성_응답.header("Location")).isNotBlank();
	}

	private void 지하철역_정보가_동일하다(StationRequest 역정보, ExtractableResponse<Response> 생성_응답) {
		String 생성된_역_이름 = 생성_응답.jsonPath().get("name");
		assertThat(생성된_역_이름).isEqualTo(역정보.getName());
	}

	private void 지하철역이_생성_실패된다(ExtractableResponse<Response> 생성_응답) {
		assertThat(생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private String 지하철_역_등록되어_있음_uri_응답(StationRequest 역정보) {
		ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청(역정보);
		return 생성_응답.header("Location");
	}

	private void 지하철역이_응답된다(ExtractableResponse<Response> 목록_조회_응답) {
		assertThat(목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철역이_포함되어_있다(ExtractableResponse<Response> 목록_조회_응답, List<String> 예상_지하철역_응답들) {
		List<Long> 예상_지하철역_아이디들 = 예상_지하철역_응답들에서_지하철역_아이디_추출하기(예상_지하철역_응답들);
		List<Long> 비교할_지하철역_아이디들 = 목록_조회_응답에서_지하철역_아이디_추출하기(목록_조회_응답);
		assertThat(비교할_지하철역_아이디들).containsAll(예상_지하철역_아이디들);
	}

	private List<Long> 목록_조회_응답에서_지하철역_아이디_추출하기(ExtractableResponse<Response> 목록_조회_응답) {
		List<Long> resultLineIds = 목록_조회_응답.jsonPath().getList(".", StationResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		return resultLineIds;
	}

	private List<Long> 예상_지하철역_응답들에서_지하철역_아이디_추출하기(List<String> 예상_지하철역_응답들) {
		List<Long> expectedLineIds = 예상_지하철역_응답들.stream()
			.map(it -> Long.parseLong(it.split("/")[2]))
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

	private ExtractableResponse<Response> 지하철역_조회_요청(String 지하철역_uri) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get(지하철역_uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 지하철역이_응답이_실패된다(ExtractableResponse<Response> 조회_응답) {
		assertThat(조회_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	private ExtractableResponse<Response> 지하철역_수정_요청(String 지하철역_uri, StationRequest 수정_역정보) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(수정_역정보)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put(지하철역_uri)
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

	private ExtractableResponse<Response> 지하철역_삭제_요청(String 지하철역_uri) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(지하철역_uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 지하철역이_삭제된다(ExtractableResponse<Response> 지하철역_삭제_응답) {
		assertThat(지하철역_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
