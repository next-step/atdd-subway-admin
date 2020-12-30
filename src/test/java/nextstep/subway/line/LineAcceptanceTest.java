package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.StationAcceptanceTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static nextstep.subway.line.LineAcceptanceTestSupport.*;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {

	private Long stationId1;

	private Long stationId2;

	private Long stationId3;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
		stationId1 = createStation("선릉역");
		stationId2 = createStation("강남역");
		stationId3 = createStation("신논현역");
	}

	private Long createStation(String name) {
		String id = StationAcceptanceTestSupport.지하철역_생성_요청(name)
				.header("Location").split("/")[2];
		return Long.parseLong(id);
	}

	@DisplayName("지하철 노선관리(생성,조회,수정,삭제) 통합 테스트")
	@Test
	void 노선관리_전체플로우() {
		// 노선 생성 - when
		ExtractableResponse<Response> created_2호선 = 지하철노선_생성_요청("2호선", "green",
				stationId1, stationId2, 10);
		ExtractableResponse<Response> created_3호선 = 지하철노선_생성_요청("3호선", "orange",
				stationId3, stationId1, 15);

		// 노선 생성 - then
		지하철노선_프로퍼티_검사(created_2호선, "2호선", "green", Arrays.asList(stationId1, stationId2));
		지하철노선_프로퍼티_검사(created_3호선, "3호선", "orange", Arrays.asList(stationId3, stationId1));

		// 노선 단일 조회 - when
		ExtractableResponse<Response> get_2호선 = 지하철노선_조회(created_2호선);
		ExtractableResponse<Response> get_3호선 = 지하철노선_조회(created_3호선);

		// 노선 단일 조회 - then
		지하철노선_조회_검사(created_2호선, get_2호선);
		지하철노선_조회_검사(created_3호선, get_3호선);

		// 노선 전체 조회 - when & then
		지하철노선목록_조회_검사(지하철노선_얻기(), created_2호선, created_3호선);

		// 노선 수정 - when
		ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(created_2호선, "2호선-개선", "grass");

		// 노선 수정 - then
		assertStatusCode(updateResponse, HttpStatus.OK);
		지하철노선_프로퍼티_검사(지하철노선_조회(created_2호선), "2호선-개선", "grass", Arrays.asList(stationId1, stationId2));

		// 노선 삭제 - when
		ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(created_2호선);

		// 노선 삭제 - then
		assertStatusCode(deleteResponse, HttpStatus.NO_CONTENT);
		지하철노선목록_조회_검사(지하철노선_얻기(), created_3호선);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void 노선생성_중복됨() {
		// given
		지하철노선_생성_요청("2호선", "green", stationId1, stationId2, 10);

		// when
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green",
				stationId2, stationId1, 10);

		// then
		assertStatusCode(response, HttpStatus.BAD_REQUEST);
	}
}
