package nextstep.subway.line;

import static nextstep.subway.LineTestHelper.노선_목록_요청;
import static nextstep.subway.LineTestHelper.노선_목록_확인;
import static nextstep.subway.LineTestHelper.노선_삭제_요청;
import static nextstep.subway.LineTestHelper.노선_생성_요청;
import static nextstep.subway.LineTestHelper.노선_수정_요청;
import static nextstep.subway.LineTestHelper.노선_조회_요청;
import static nextstep.subway.LineTestHelper.노선_조회_확인;
import static nextstep.subway.TestHelper.getId;
import static nextstep.subway.TestHelper.삭제됨_확인;
import static nextstep.subway.TestHelper.생성됨_확인;
import static nextstep.subway.TestHelper.이름_불포함_확인;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.Test;

public class LineAcceptanceTest extends BaseAcceptanceTest {

    private static final String 신분당선 = "신분당선";
    private static final String 분당선 = "분당선";
    private static final String RED_COLOR = "bg-red-600";
    private static final String GREEN_COLOR = "bg-green-600";
    private static final String 강남역 = "강남역";
    private static final String 역삼역 = "역삼역";
    private static final String 서초역 = "서초역";
    private static final String 삼성역 = "삼성역";
    private static final int DISTANCE_10 = 10;
    private static final int DISTANCE_20 = 20;

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    public void createLine() {
        // when
        ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(신분당선, RED_COLOR, DISTANCE_10, 강남역, 역삼역);
        생성됨_확인(노선_생성_응답);

        //then
        ExtractableResponse<Response> 노선_목록 = 노선_목록_요청();
        노선_목록_확인(노선_목록, 신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    public void getLines() {
        // given
        노선_생성_요청(신분당선, RED_COLOR, DISTANCE_10, 강남역, 역삼역);
        노선_생성_요청(분당선, GREEN_COLOR, DISTANCE_20, 서초역, 삼성역);

        // when
        ExtractableResponse<Response> 노선_목록 = 노선_목록_요청();

        // then
        노선_목록_확인(노선_목록, 신분당선, 분당선);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    public void getLine() {
        // given
        int distance = 10;
        ExtractableResponse<Response> 노선_생성_응답
            = 노선_생성_요청(신분당선, RED_COLOR, distance, 강남역, 역삼역);
        Long id = getId(노선_생성_응답);

        // when
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(id);

        // then
        노선_조회_확인(신분당선, RED_COLOR, distance, 노선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    public void updateLine() {
        // given
        ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(신분당선, RED_COLOR, DISTANCE_10, 강남역, 역삼역);
        Long id = getId(노선_생성_응답);

        // when
        노선_수정_요청(id, 분당선, GREEN_COLOR, DISTANCE_20);

        // then
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(id);
        노선_조회_확인(분당선, GREEN_COLOR, DISTANCE_20, 노선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    public void removeLine() {
        // given
        ExtractableResponse<Response> 생성_응답 = 노선_생성_요청(신분당선, RED_COLOR, DISTANCE_10, 강남역, 역삼역);
        Long id = getId(생성_응답);

        // when
        ExtractableResponse<Response> 삭제_응답 = 노선_삭제_요청(id);
        삭제됨_확인(삭제_응답);

        // then
        ExtractableResponse<Response> 노선_목록_응답 = 노선_목록_요청();
        이름_불포함_확인(노선_목록_응답, 신분당선);
    }

}
