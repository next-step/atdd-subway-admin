package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
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

    private Long 노선ID;
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

        노선ID = 지하철_노선_등록되어_있음_toId("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);
    }

    @DisplayName("setUp 에서 `노선ID` 생성됬는지 체크")
    @Test
    void 노선_생성() {
        // given
        // when
        // then
        assertThat(노선ID).isNotNull();
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 노선_중복_생성_실패() {
        // given
        // when
        ExtractableResponse<Response> 노선등록응답 = 지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);

        // then
        노선_등록_실패(노선등록응답, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 노선_전체_목록_조회() {
        // given
        Long 새로운노선ID = 지하철_노선_등록되어_있음_toId("3호선", "ORANGE", 잠실역ID, 강동구청역ID, 100);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        노선_조회_노선ID_포함됨(response, Arrays.asList(노선ID, 새로운노선ID));
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 노선_조회() {
        // given
        // when
        ExtractableResponse<Response> 노선조회응답 = 지하철_노선_조회_요청(노선ID);

        // then
        노선_등록_실패(노선조회응답, HttpStatus.OK);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 노선_수정() {
        // given
        Map<String, String> 노선수정파라미터 = 지하철_노선_생성_파라미터_맵핑("3호선", "RED");

        // when
        ExtractableResponse<Response> 노선수정요청응답 = 지하철_노선_수정_요청(노선수정파라미터, 노선ID);

        // then
        노선_수정_성공(노선수정파라미터, 노선수정요청응답);
    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void 노선_삭제() {
        // given
        // when
        ExtractableResponse<Response> 노선제거응답 = 지하철_노선_제거_요청(노선ID);

        // then
        노선_등록_실패(노선제거응답, HttpStatus.NO_CONTENT);
    }

}
