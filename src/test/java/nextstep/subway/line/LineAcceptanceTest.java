package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.LineRestAssuredUtils.*;
import static nextstep.subway.utils.StationRestAssuredUtils.지하철_역_생성_요청;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600", "1","2","10");

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");
        지하철_노선_생성_요청("신분당선", "bg-red-600", "1","2","10");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600", "1","2","10");

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");
        지하철_노선_생성_요청("2호선", "green", "1","2","10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<String> lineNames = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(lineResponse -> lineResponse.getName())
                .collect(Collectors.toList());
        Assertions.assertThat(lineNames).contains("2호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");
        ExtractableResponse<Response> request = 지하철_노선_생성_요청("신분당선", "bg-red-600", "1","2","10");

        // when
        // 지하철_노선_조회_요청
        LineResponse lineResponse = request.jsonPath().getObject(".", LineResponse.class);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        // 지하철_노선_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void lineNotFoundException() {
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1l);

        // then
        // 지하철_노선_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");
        ExtractableResponse<Response> request = 지하철_노선_생성_요청("4호선", "blue","1","2","10");

        // when
        // 지하철_노선_수정_요청
        LineResponse lineResponse = request.jsonPath().getObject(".", LineResponse.class);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse.getId(), "2호선", "green");

        // then
        // 지하철_노선_수정됨
        LineResponse finalResponse = response.jsonPath().getObject(".", LineResponse.class);
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(finalResponse.getName()).isEqualTo("2호선");
        Assertions.assertThat(finalResponse.getColor()).isEqualTo("green");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");
        ExtractableResponse<Response> request = 지하철_노선_생성_요청("4호선", "blue", "1","2","10");

        // when
        // 지하철_노선_제거_요청
        LineResponse lineResponse = request.jsonPath().getObject(".", LineResponse.class);
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(lineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
