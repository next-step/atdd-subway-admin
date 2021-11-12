package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.HttpUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600");
    private static LineRequest 이호선 = new LineRequest("2호선", "green");
    
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest request = 신분당선;

        // when
        ExtractableResponse<Response> response = post("/lines", request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        post("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = post("/lines", 신분당선);

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLine() {
        // given
        LineRequest request1 = 신분당선;
        ExtractableResponse<Response> response1 = post("/lines", request1);

        LineRequest request2 = 이호선;
        ExtractableResponse<Response> response2 = post("/lines", request2);

        // when
        ExtractableResponse<Response> actual = get("/lines");

        // then
        List<Long> expectedLineIds = Arrays.asList(response1, response2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[1]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = actual.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findOneLine() {
        // given
        LineRequest request = 신분당선;
        Long id = post("/lines", request).as(LineResponse.class).getId();

        // when
        ExtractableResponse<Response> actual = get("/lines/{id}", id);

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.as(LineResponse.class).getId()).isEqualTo(id);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest request = 신분당선;
        LineResponse response = post("/lines", request).as(LineResponse.class);

        // when
        ExtractableResponse<Response> actual = put("/lines/{id}", 이호선, response.getId());

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest request = 신분당선;
        LineResponse response = post("/lines", request).as(LineResponse.class);

        // when
        ExtractableResponse<Response> actual = delete("/lines/{id}", response.getId());

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


}
