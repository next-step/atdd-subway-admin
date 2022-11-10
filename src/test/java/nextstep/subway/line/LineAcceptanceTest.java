package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.LineAcceptanceStep.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성하면 응답상태 201을 반환한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청("1호선", "indigo darken-2");

        // then
        지하철노선_생성_응답상태_201_검증(response);
    }

    @DisplayName("이미 등록된 지하철 노선 이름으로 지하철 노선을 생성하면 응답상태 400을 반환한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        등록된_지하철노선("1호선", "indigo darken-2");

        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청("1호선", "indigo darken-2");

        // then
        지하철노선_생성_응답상태_400_검증(response);
    }

    @DisplayName("지하철 노선 목록을 조회하면 응답상태 200을 반환한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 등록된_지하철노선("1호선", "indigo darken-2");
        ExtractableResponse<Response> createResponse2 = 등록된_지하철노선("2호선", "green darken-1");

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        // then
        지하철노선_목록_조회_응답상태_200_검증(response);
        지하철노선_목록_검증(response, Arrays.asList(createResponse1, createResponse2));
    }

    @DisplayName("지하철 노선을 조회하면 응답상태 200을 반환한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 등록된_지하철노선("1호선", "indigo darken-2");

        // when
        ExtractableResponse<Response> response = 지하철노선_조회_요청(createResponse);

        // then
        지하철노선_조회_응답상태_200_검증(response);
    }

    @DisplayName("지하철 노선을 수정하면 응답상태 ")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 등록된_지하철노선("1호선", "indigo darken-2");

        // when
        ExtractableResponse<Response> response = 지하철노선_수정_요청(createResponse, "우이신설선", "yellow darken-3");

        // then
        지하철노선_수정_응답상태_200_검증(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 등록된_지하철노선("1호선", "indigo darken-2");

        // when
        ExtractableResponse<Response> response = 지하철노선_제거_요청(createResponse);

        // then
        지하철_노선_삭제_응답상태_204_검증(response);
    }
}
