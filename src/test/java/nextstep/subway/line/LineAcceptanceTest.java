package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.RestAssuredTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static nextstep.subway.PageController.URIMapping.LINE;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static RestAssuredTemplate restAssuredTemplate;
    private static Map<String, String> param1;
    private static Map<String, String> param2;

    @BeforeAll
    public static void setup() {
        restAssuredTemplate = new RestAssuredTemplate(LINE);

        param1 = new HashMap<String, String>(){
            {
                put("name", "1호선");
                put("color", "blue lighten-1");
            }
        };

        param2 = new HashMap<String, String>(){
            {
                put("name", "2호선");
                put("color", "green lighten-1");
            }
        };
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreatedLine(param1);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        requestCreatedLine(param1);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreatedLine(param1);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = requestCreatedLine(param1);
        ExtractableResponse<Response> createResponse2 = requestCreatedLine(param2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestShowLines();

        // then
        assertAll(
            // 지하철_노선_목록_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),

            // 지하철_노선_목록_포함됨
            () -> {
                List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                        .map(restAssuredTemplate::getLocationId)
                        .collect(Collectors.toList());
                List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                        .map(it -> it.getId())
                        .collect(Collectors.toList());

                assertThat(resultLineIds).containsAll(expectedLineIds);
            }
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = requestCreatedLine(param1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestShowLines(restAssuredTemplate.getLocationId(createResponse));

        // then
        assertAll(
            // 지하철_노선_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),

            // 지하철_노선_포함됨
            () -> {
                Long expected = response.as(LineResponse.class).getId();
                assertThat(restAssuredTemplate.getLocationId(createResponse)).isEqualTo(expected);
            }
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = requestCreatedLine(param1);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(restAssuredTemplate.getLocationId(createResponse), param2);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정할때, 기존에 존재하는 노선 이름으로는 변경할수 없다.")
    @Test
    void updateLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = requestCreatedLine(param1);
        ExtractableResponse<Response> createResponse2 = requestCreatedLine(param2);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(restAssuredTemplate.getLocationId(createResponse1), param2);

        // then
        // 지하철_노선_수정_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = requestCreatedLine(param1);

        // when
        // 지하철_노선_제거_요청
        long deletedId = restAssuredTemplate.getLocationId(createResponse);
        ExtractableResponse<Response> deletedResponse = requestDeleteLine(deletedId);

        // then
        // 지하철_노선_삭제됨
        assertAll(
            // 지하철_노선삭제
            () -> assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),

            // 지하철_노선_찾지못함
            () -> {
                ExtractableResponse<Response> response = requestShowLines(deletedId);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            }
        );
    }

    private ExtractableResponse<Response> requestShowLines() {
        return restAssuredTemplate.get();
    }

    private ExtractableResponse<Response> requestShowLines(final Long id) {
        return restAssuredTemplate.get(id);
    }

    private ExtractableResponse<Response> requestCreatedLine(final Map<String, String> param) {
        return restAssuredTemplate.post(param);
    }

    private ExtractableResponse<Response> requestUpdateLine(final Long id, final Map<String, String> param) {
        return restAssuredTemplate.put(id, param);
    }

    private ExtractableResponse<Response> requestDeleteLine(final Long id) {
        return restAssuredTemplate.delete(id);
    }
}
