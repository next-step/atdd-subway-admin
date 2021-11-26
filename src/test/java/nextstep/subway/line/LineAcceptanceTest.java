package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.StationAcceptanceTestUtil.*;
import static nextstep.subway.utils.LineAcceptanceTestUtil.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long 잠실역ID;
    private Long 몽촌토성역ID;
    private Long 강동구청역ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        잠실역ID = 지하철됨_역_생성_됨_toId("잠실역");
        몽촌토성역ID = 지하철됨_역_생성_됨_toId("몽촌토성역");
        강동구청역ID = 지하철됨_역_생성_됨_toId("강동구청역");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> 노선_등록_응답 = 지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);

        // then
        assertAll(
            () -> assertThat(노선_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(노선_등록_응답.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);

        // when
        ExtractableResponse<Response> 노선_등록_응답 = 지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);

        // then
        assertThat(노선_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> 노선_등록_응답1 = 지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);
        ExtractableResponse<Response> 노선_등록_응답2 = 지하철_노선_등록되어_있음("3호선", "ORANGE", 잠실역ID, 강동구청역ID,
            100);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        List<Long> 예상_등록_노선_ID_목록 = ids_추출_ByLocation(Arrays.asList(노선_등록_응답1, 노선_등록_응답2));
        List<Long> 결과_등록_노선_ID_목록 = ids_추출_ByLineResponse(response);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(결과_등록_노선_ID_목록).containsAll(예상_등록_노선_ID_목록)
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> 노선_등록_응답 = 지하철_노선_등록되어_있음("2호선",
            "RED", 잠실역ID, 몽촌토성역ID, 100);

        // when
        ExtractableResponse<Response> 노선_조회_응답 = 지하철_노선_조회_요청(
            노선_등록_응답);

        // then
        assertThat(노선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 노선_등록_응답 = 지하철_노선_등록되어_있음("2호선",
            "RED", 잠실역ID, 몽촌토성역ID, 100);
        Map<String, String> 노선_수정_파라미터 = 지하철_노선_생성_파라미터_맵핑("3호선", "RED");

        // when
        ExtractableResponse<Response> 노선_수정_요청_응답 = 지하철_노선_수정_요청(노선_수정_파라미터,
            노선_등록_응답);

        // then
        String 수정된_노선_이름 = 노선_수정_요청_응답.jsonPath().get("name");
        assertAll(
            () -> assertThat(노선_수정_요청_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(수정된_노선_이름).isEqualTo(노선_수정_파라미터.get("name"))
        );
    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 노선_등록_응답 = 지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);

        // when
        ExtractableResponse<Response> 노선_제거_응답 = 지하철_노선_제거_요청(노선_등록_응답);

        // then
        assertThat(노선_제거_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
