package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestUtils.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = requestToCreateLine(params);

        // then
        createLineSuccess(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        lineCreated("신분당선", "red");
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = requestToCreateLine(params);

        // then
        createLineFail(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        long id1 = lineCreated("신분당선", "red");
        long id2 = lineCreated("2호선", "green");
        LocalDateTime now = LocalDateTime.now();
        List<LineResponse> expected = Arrays.asList(new LineResponse(id1, "신분당선", "red", now, now),
            new LineResponse(id2, "2호선", "green", now, now));

        // when
        ExtractableResponse<Response> response = requestToCreateLineList();

        // then
        createLineListSuccess(response);
        lineListContainsExpected(response, expected);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long id = lineCreated("신분당선", "red");
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "신분당선", "red", now, now);

        // when
        ExtractableResponse<Response> response = reqeustToGetLine(id);

        // then
        getLineSuccess(response, expected);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회하여 실패한다.")
    @Test
    void getLine_throwsExceptionWHenNoExist() {
        // given

        // when
        ExtractableResponse<Response> response = reqeustToGetLine(1L);

        // then
        getLineNotFound(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long id = lineCreated("신분당선", "red");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "2호선", "green", now, now);

        // when
        ExtractableResponse<Response> response = requestToUpdateLine(id, params);

        // then
        updateLineSuccess(response, expected);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정하여 실패한다.")
    @Test
    void updateLine_throwsExceptionWHenNoExist() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        // when
        ExtractableResponse<Response> response = requestToUpdateLine(1L, params);

        // then
        getLineNotFound(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        long id = lineCreated("신분당선", "red");

        // when
        ExtractableResponse<Response> response = requestToDeleteLine(id);

        // then
        lineDeleteSuccess(response);
    }
}
