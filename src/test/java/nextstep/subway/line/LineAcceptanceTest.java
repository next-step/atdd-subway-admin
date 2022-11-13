package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.AcceptanceFixture.*;
import static nextstep.subway.line.LineAcceptanceFixture.*;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void create_line() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");

        // when
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청(
                "강남잠실노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10
        );

        // then
        assertThat(지하철_노선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("같은 이름으로 지하철노선 생성 오류")
    @Test
    void same_name_create_line_error() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        지하철_노선_생성_요청("강남잠실노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10);

        // when
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청(
                "강남잠실노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10
        );

        // then
        assertThat(지하철_노선_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void get_line_list() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        지하철_노선_생성_요청("1번지하철노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10);
        지하철_노선_생성_요청("2번지하철노선", "yellow", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_응답 = 지하철_노선_목록_조회_요청();
        List<String> 지하철_노선_목록_이름 = 목록_이름_조회(지하철_노선_목록_조회_응답);

        // then
        assertThat(지하철_노선_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_목록_이름).contains("1번지하철노선", "2번지하철노선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void get_line() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청(
                "1번지하철노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10
        );

        // when
        Long id = 식별_아이디_조회(지하철_노선_생성_응답);
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(id);
        String 지하철_노선_이름 = 이름_조회(지하철_노선_조회_응답);

        // then
        assertThat(지하철_노선_이름).isEqualTo("1번지하철노선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void update_line() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청(
                "1번지하철노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10
        );

        // when
        Long 지하철역_아이디 = 식별_아이디_조회(지하철_노선_생성_응답);
        LineRequest 수정_정보 = new LineRequest("지하철노선수정", "blue");
        ExtractableResponse<Response> 지하철_노선_수정_응답 = 지하철_노선_수정_요청(지하철역_아이디, 수정_정보);

        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(지하철역_아이디);
        String 수정된_지하철_노선_이름 = 이름_조회(지하철_노선_조회_응답);

        // then
        assertThat(지하철_노선_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(수정된_지하철_노선_이름).isEqualTo("지하철노선수정");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void delete_line() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청(
                "1번지하철노선", "red", 식별_아이디_조회(강남역), 식별_아이디_조회(잠실역), 10
        );

        // when
        Long 지하철역_아이디 = 식별_아이디_조회(지하철_노선_생성_응답);
        ExtractableResponse<Response> 지하철_노선_삭제_응답 = 지하철_노선_삭제_요청(지하철역_아이디);

        // then
        assertThat(지하철_노선_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


}
