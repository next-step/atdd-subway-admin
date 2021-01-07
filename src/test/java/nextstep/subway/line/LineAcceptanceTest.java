package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends LineAcceptanceMethod {
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Map<String, String> params = createLine2Params();

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

		// then
		assertAll(
			() -> assertThat(response).isNotNull(),
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.as(LineResponse.class).getId()).isNotNull()
		);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		Map<String, String> params = createLine2Params();
		지하철_노선_생성_요청(params);

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선전체를 조회한다.")
	@Test
	void getLines() {
		// given
		지하철_노선_생성_요청(createLine1Params());
		지하철_노선_생성_요청(createLine2Params());

		// when
		ExtractableResponse<Response> response = 지하철_노선_전체_조회_요청();
		List<LineResponse> lines = response.jsonPath().getList(".", LineResponse.class);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(lines).hasSize(2)
		);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		LineResponse line = 지하철_2호선_생성요청();

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.as(LineResponse.class)).isNotNull(),
			() -> assertThat(response.as(LineResponse.class).getId()).isNotNull(),
			() -> assertThat(response.as(LineResponse.class).getStations()).hasSize(2)

		);
	}

	@DisplayName("지하철 없는 노선 조회한다.")
	@Test
	void getLineFailTest() {
		// given
		LineResponse line = 지하철_2호선_생성요청();

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId() + 1);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		LineResponse line = 지하철_2호선_생성요청();
		Map<String, String> params = new HashMap<>();
		params.put("name", "3호선");
		params.put("color", "orange");

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(line.getId(), params);

		// then
		assertAll(
			() -> assertThat(response).isNotNull(),
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.as(LineResponse.class)).isNotEqualTo(line),
			() -> assertThat(response.as(LineResponse.class).getId()).isEqualTo(line.getId()),
			() -> assertThat(response.as(LineResponse.class).getName()).isEqualTo("3호선")
		);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		LineResponse line = 지하철_2호선_생성요청();

		// when
		ExtractableResponse<Response> response = 지하철_노선_삭제_요청(line.getId());

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
			() -> assertThat(지하철_노선_조회_요청(line.getId()).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("지하철 잘못된 노선을 제거한다.")
	@Test
	void deleteFailLine() {
		// given
		LineResponse line = 지하철_2호선_생성요청();

		// when
		ExtractableResponse<Response> response = 지하철_노선_삭제_요청(line.getId() + 1);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("지하철 노선에 구간을 추가한다.")
	@Test
	void addSectionLineTest() {
		// given
		LineResponse line = 지하철_2호선_생성요청();
		String 시청ID = line.getStations().get(0).getId().toString();
		String 서초ID = line.getStations().get(1).getId().toString();
		String 홍대ID = createStationId("성수역");
		String 신촌ID = createStationId("신촌역");
		String 성수ID = createStationId("홍대역");
		String 강남ID = createStationId("강남역");
		Map<String, String> 앞_맨앞_추가_요청 = createSectionParam(성수ID, 시청ID, "20");
		Map<String, String> 앞_중간_추가_요청 = createSectionParam(시청ID, 신촌ID, "10");
		Map<String, String> 뒤_중간_추가_요청 = createSectionParam(홍대ID, 서초ID, "50");
		Map<String, String> 뒤_맨뒤_추가_요청 = createSectionParam(서초ID, 강남ID, "20");

		// when
		ExtractableResponse<Response> 앞_맨앞_추가_결과 = 지하철_노선_구간추가_요청(line.getId(), 앞_맨앞_추가_요청);
		ExtractableResponse<Response> 앞_중간_추가_결과 = 지하철_노선_구간추가_요청(line.getId(), 앞_중간_추가_요청);
		ExtractableResponse<Response> 뒤_중간_추가_결과 = 지하철_노선_구간추가_요청(line.getId(), 뒤_중간_추가_요청);
		ExtractableResponse<Response> 뒤_맨뒤_추가_결과 = 지하철_노선_구간추가_요청(line.getId(), 뒤_맨뒤_추가_요청);
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

		// then
		assertAll(
			() -> assertThat(앞_맨앞_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(앞_중간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(뒤_중간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(뒤_맨뒤_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.as(LineResponse.class).getStations()).hasSize(6)
		);
	}

	@DisplayName("지하철 노선에 구간을 추가할때, 예외처리를 한다.")
	@Test
	void addSectionLineExceptionTest() {
		// given
		LineResponse line = 지하철_2호선_생성요청();
		String 시청ID = line.getStations().get(0).getId().toString();
		String 서초ID = line.getStations().get(1).getId().toString();
		String 홍대ID = createStationId("홍대역");
		String 강남ID = createStationId("강남역");

		// when
		ExtractableResponse<Response> over = 지하철_노선_구간추가_요청(line.getId(), createSectionParam(홍대ID, 서초ID, "110"));
		ExtractableResponse<Response> exist = 지하철_노선_구간추가_요청(line.getId(), createSectionParam(서초ID, 시청ID, "20"));
		ExtractableResponse<Response> nothing = 지하철_노선_구간추가_요청(line.getId(), createSectionParam(홍대ID, 강남ID, "30"));
		// then
		assertAll(
			() -> assertThat(over.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
			() -> assertThat(exist.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
			() -> assertThat(nothing.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("지하철 구간 삭제요청 했을때, 구간이 한개라면 삭제되지 않는다.")
	@Test
	void deleteOneSectionTest() {
		// given
		LineResponse line = 지하철_2호선_생성요청();

		// when
		ExtractableResponse<Response> response = 지하철_노선_구간삭제_요청(line.getId(), "1");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 구간을 삭제한다.")
	@Test
	void deleteSection() {
		// given
		LineResponse line = 지하철_2호선_생성요청();
		String 성수ID = createStationId("성수역");
		지하철_구간_추가(line, 성수ID, "20");

		// when
		ExtractableResponse<Response> response = 지하철_노선_구간삭제_요청(line.getId(), 성수ID);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("지하철 구간 삭제요청 했을때, 없는 구간이라면 삭제되지 않는다.")
	@Test
	void deleteNoSectionTest() {
		// given
		LineResponse line = 지하철_2호선_생성요청();
		String 홍대ID = createStationId("홍대역");
		String 성수ID = createStationId("성수역");
		지하철_구간_추가(line, 성수ID, "20");

		// when
		ExtractableResponse<Response> response = 지하철_노선_구간삭제_요청(line.getId(), 홍대ID);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
