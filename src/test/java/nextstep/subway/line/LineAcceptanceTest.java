package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.LineTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = LineTestUtil.지하철_노선_생성_파라미터_맵핑("2호선");

        // when
        ExtractableResponse<Response> response = LineTestUtil.지하철_노선_생성_요청(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        Map<String, String> params = LineTestUtil.지하철_노선_생성_파라미터_맵핑("2호선");
        LineTestUtil.지하철_노선_생성_요청(params);

        // when
        ExtractableResponse<Response> response = LineTestUtil.지하철_노선_생성_요청(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = LineTestUtil.지하철_노선_등록되어_있음("2호선");
        ExtractableResponse<Response> createResponse2 = LineTestUtil.지하철_노선_등록되어_있음("3호선");

        // when
        ExtractableResponse<Response> response = LineTestUtil.지하철_노선_목록_조회_요청("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = LineTestUtil.ids_추출_By_Location(createResponse1,
            createResponse2);
        List<Long> resultLineIds = LineTestUtil.ids_추출_By_LineResponse(response);

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse1 = LineTestUtil.지하철_노선_등록되어_있음("2호선");
        long lineId = LineTestUtil.ids_추출_By_Location(createResponse1).get(0);

        // when
        ExtractableResponse<Response> response = LineTestUtil.지하철_노선_목록_조회_요청("/lines/" + lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse1 = LineTestUtil.지하철_노선_등록되어_있음("2호선");
        long lineId = Long.parseLong(createResponse1.header("Location").split("/")[2]);
        Map<String, String> updateParams = LineTestUtil.지하철_노선_생성_파라미터_맵핑("3호선");

        // when
        ExtractableResponse<Response> response = LineTestUtil.지하철_노선_수정_요청(updateParams, lineId);

        // then
        String responseLineName = response.jsonPath().get("name");
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(responseLineName).isEqualTo(updateParams.get("name"))
        );
    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = LineTestUtil.지하철_노선_등록되어_있음("2호선");

        // when
        ExtractableResponse<Response> response = LineTestUtil.지하철_노선_제거_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
