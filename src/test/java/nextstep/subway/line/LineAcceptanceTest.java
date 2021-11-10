package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.LineStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest 신분당선_요청;
    private LineRequest 이호선_요청;

    @BeforeEach
    public void setUp() {
        super.setUp();

        신분당선_요청 = new LineRequest("신분당선", "bg-red-600");
        이호선_요청 = new LineRequest("2호선", "bg-green-600");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_요청);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음(신분당선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_요청);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선_요청);
        LineResponse 이호선_응답 = 지하철_노선_등록되어_있음(이호선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(신분당선_응답, 이호선_응답));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선_요청);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
                                                    .given().log().all()
                                                    .accept(MediaType.APPLICATION_JSON_VALUE)
                                                    .when().get("/lines/{id}", 신분당선_응답.getId())
                                                    .then().log().all()
                                                    .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isEqualTo(신분당선_응답);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }
}
