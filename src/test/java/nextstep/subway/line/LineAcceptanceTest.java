package nextstep.subway.line;

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
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	private LineRequest 이호선;
	private LineRequest 삼호선_이호선과_같은_색상;
	private LineRequest 사호선;

	@BeforeEach
	void 초기화() {
		이호선 = new LineRequest("2호선", "#FFFFFF");
		삼호선_이호선과_같은_색상 = new LineRequest("3호선", "#FFFFFF");
		사호선 = new LineRequest("4호선", "#000000");
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(이호선);

		// then
		노선이_생성된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 지하철 노선 이름을 전달하지 않으면 등록할 수 없다.")
	@Test
	void createLineWithNullName() {
		// given
		LineRequest 노선이름_없음 = new LineRequest(null, "#FFFFFF");

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(노선이름_없음);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 지하철 노선 이름을 공백(\"\")으로 전달하면 등록할 수 없다.")
	@Test
	void createLineWithBlankName() {
		// given
		LineRequest 노선이름_공백 = new LineRequest("", "#FFFFFF");

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(노선이름_공백);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 지하철 노선 이름을 상당히 길게 작성하면 등록할 수 없다.")
	@Test
	void createLineWithLongName() {
		// given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		LineRequest 노선이름_너무_김 = new LineRequest(이백오십육바이트_이름, "#FFFFFF");

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(노선이름_너무_김);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 지하철 노선 색상을 전달하지 않으면 등록할 수 없다.")
	@Test
	void createLineWithNullColor() {
		// given
		LineRequest 색상_없음 = new LineRequest("1호선", null);

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(색상_없음);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 지하철 노선 색상을 공백(\"\")으로 전달하면 등록할 수 없다.")
	@Test
	void createLineWithBlankColor() {
		// given
		LineRequest 색상_공백 = new LineRequest("1호선", "");

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(색상_공백);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 지하철 노선 색상을 상당히 길게 작성하면 등록할 수 없다.")
	@Test
	void createLineWithLongColor() {
		// given
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";
		LineRequest 색상_너무_김 = new LineRequest("1호선", 이백오십육바이트_색상);

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(색상_너무_김);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 기존에 존재하는 지하철 노선 색상으로 전달해도 지하철 노선을 생성할 수 있다.")
	@Test
	void createLineWithDuplicateColor() {
		// given
		노선_생성_요청(이호선);

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(삼호선_이호선과_같은_색상);

		// then
		노선이_생성된다(생성_응답);
	}

	@DisplayName("지하철 노선을 등록할 경우 기존에 존재하는 지하철 노선 이름으로 작성하면 등록할 수 없다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		노선_생성_요청(이호선);

		// when
		ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(이호선);

		// then
		노선이_생성_실패된다(생성_응답);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> 초록색_라인_생성_응답 = 노선_생성_요청(이호선);
		ExtractableResponse<Response> 파란색_라인_생성_응답 = 노선_생성_요청(사호선);

		// when
		ExtractableResponse<Response> 목록_조회_응답 = 노선_목록_조회_요청();

		// then
		노선이_응답된다(목록_조회_응답);
		노선이_포함되어_있다(초록색_라인_생성_응답, 파란색_라인_생성_응답, 목록_조회_응답);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);

		// when
		ExtractableResponse<Response> 조회_응답 = 노선_조회_요청(노선_생성_응답);

		// then
		노선이_응답된다(조회_응답);
		노선이_동일하다(노선_생성_응답, 조회_응답);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 사호선);

		// then
		노선이_수정된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 지하철 노선 이름을 전달하지 않으면 수정할 수 없다.")
	@Test
	void updateLineWithNullName() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);
		LineRequest 노선이름_없는_파라메터 = new LineRequest(null, "#FFFFFF");

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 노선이름_없는_파라메터);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 지하철 노선 이름을 공백(\"\")으로 전달하면 수정할 수 없다.")
	@Test
	void updateLineWithBlankName() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);
		LineRequest 공백_노선이름_파라메터 = new LineRequest("", "#FFFFFF");

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 공백_노선이름_파라메터);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 지하철 노선 이름을 상당히 길게 작성하면 수정할 수 없다.")
	@Test
	void updateLineWithLongName() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		LineRequest 긴_노선이름_파라메터 = new LineRequest(이백오십육바이트_이름, "#FFFFFF");

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 긴_노선이름_파라메터);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 지하철 노선 색상을 전달하지 않으면 수정할 수 없다.")
	@Test
	void updateLineWithNullColor() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);
		LineRequest 색상_없는_파라메터 = new LineRequest("1호선", null);

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 색상_없는_파라메터);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 지하철 노선 색상을 공백(\"\")으로 전달하면 수정할 수 없다.")
	@Test
	void updateLineWithBlankColor() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);
		LineRequest 공백_색상_파라메터 = new LineRequest("1호선", "");

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 공백_색상_파라메터);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 지하철 노선 색상을 상당히 길게 작성하면 수정할 수 없다.")
	@Test
	void updateLineWithLongColor() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";
		LineRequest 긴_색상_파라메터 = new LineRequest("2호선", 이백오십육바이트_색상);

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 긴_색상_파라메터);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 기존에 존재하는 지하철 노선 색상으로 지하철 노선을 수정할 수 있다.")
	@Test
	void updateLineWithDuplicateColor() {
		// given
		ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(이호선);

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(노선_생성_응답, 삼호선_이호선과_같은_색상);

		// then
		노선이_수정된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 수정할 경우 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정할 수 없다.")
	@Test
	void updateLineWithDuplicateName() {
		// given
		ExtractableResponse<Response> 초록색_라인_생성_응답 = 노선_생성_요청(이호선);
		ExtractableResponse<Response> 파란색_라인_생성_응답 = 노선_생성_요청(사호선);

		// when
		ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(파란색_라인_생성_응답, 이호선);

		// then
		노선이_수정_실패된다(노선_수정_응답);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> 초록색_라인_생성_응답 = 노선_생성_요청(이호선);

		// when
		ExtractableResponse<Response> 노선_삭제_응답 = 노선_삭제_요청(초록색_라인_생성_응답);

		// then
		노선이_삭제된다(노선_삭제_응답);
	}

	private ExtractableResponse<Response> 노선_생성_요청(LineRequest params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private void 노선이_생성된다(ExtractableResponse<Response> 생성_응답) {
		assertThat(생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(생성_응답.header("Location")).isNotBlank();
	}

	private void 노선이_생성_실패된다(ExtractableResponse<Response> 생성_응답) {
		assertThat(생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private void 노선이_응답된다(ExtractableResponse<Response> 목록_조회_응답) {
		assertThat(목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 노선이_포함되어_있다(ExtractableResponse<Response> 초록색_라인_생성_응답,
		ExtractableResponse<Response> 파란색_라인_생성_응답,
		ExtractableResponse<Response> 목록_조회_응답) {
		List<Long> 예상_노선_아이디들 = 생성_응답에서_노선_아이디_추출하기(초록색_라인_생성_응답, 파란색_라인_생성_응답);
		List<Long> 비교할_노선_아이디들 = 목록_조회_응답에서_노선_아이디_추출하기(목록_조회_응답);
		assertThat(비교할_노선_아이디들).containsAll(예상_노선_아이디들);
	}

	private List<Long> 목록_조회_응답에서_노선_아이디_추출하기(ExtractableResponse<Response> 목록_조회_응답) {
		List<Long> resultLineIds = 목록_조회_응답.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		return resultLineIds;
	}

	private List<Long> 생성_응답에서_노선_아이디_추출하기(ExtractableResponse<Response> 초록색_라인_생성_응답,
		ExtractableResponse<Response> 파란색_라인_생성_응답) {
		List<Long> expectedLineIds = Arrays.asList(초록색_라인_생성_응답, 파란색_라인_생성_응답).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		return expectedLineIds;
	}

	private ExtractableResponse<Response> 노선_목록_조회_요청() {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
		return response;
	}

	private ExtractableResponse<Response> 노선_조회_요청(ExtractableResponse<Response> 초록색_라인_생성_응답) {
		String uri = 초록색_라인_생성_응답.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get(uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 노선이_동일하다(ExtractableResponse<Response> 노선_생성_응답, ExtractableResponse<Response> 조회_응답) {
		LineResponse 생성시_응답 = 노선_생성_응답.jsonPath().getObject(".", LineResponse.class);
		LineResponse 조회후_응답 = 조회_응답.jsonPath().getObject(".", LineResponse.class);
		assertThat(생성시_응답.equals(조회후_응답)).isTrue();
	}

	private ExtractableResponse<Response> 노선_수정_요청(ExtractableResponse<Response> 노선_생성_응답,
		LineRequest 파라메터) {
		String uri = 노선_생성_응답.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(파라메터)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put(uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 노선이_수정된다(ExtractableResponse<Response> 노선_수정_응답) {
		assertThat(노선_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 노선이_수정_실패된다(ExtractableResponse<Response> 노선_수정_응답) {
		assertThat(노선_수정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 노선_삭제_요청(ExtractableResponse<Response> 노선_생성_응답) {
		String uri = 노선_생성_응답.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();
		return response;
	}

	private void 노선이_삭제된다(ExtractableResponse<Response> 노선_삭제_응답) {
		assertThat(노선_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
