package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 실패 테스트")
public class LineAcceptanceFailureTest extends AcceptanceTest {

    /**
     * When 지하철 노선 이름을 null로 입력해 지하철 노선을 생성하면
     * Then 노선을 생성할 수 없다
     */
    @DisplayName("지하철 노선 이름으로 null을 입력해 지하철 노선을 생성한다.")
    @Test
    void createLineWithNullTypeName() {
        // when
        ExtractableResponse<Response> response =
                LineAcceptance.create_line(null, "bg-red-600", 1L, 2L, 10);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * When 지하철 노선 색상을 null로 입력해 지하철 노선을 생성하면
     * Then 노선을 생성할 수 없다
     */
    @DisplayName("지하철 노선 색상으로 null을 입력해 지하철 노선을 생성한다.")
    @Test
    void createLineWithNullTypeColor() {
        // when
        ExtractableResponse<Response> response =
                LineAcceptance.create_line("신분당선", null, 1L, 2L, 10);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * When 지하철 상행 종점역 Id를 null로 입력해 지하철 노선을 생성하면
     * Then 노선을 생성할 수 없다
     */
    @DisplayName("지하철 상행 종점역 Id를 null을 입력해 지하철 노선을 생성한다.")
    @Test
    void createLineWithNullTypeUpStationId() {
        // when
        ExtractableResponse<Response> response =
                LineAcceptance.create_line("신분당선", "bg-red-600", null, 2L, 10);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * When 지하철 하행 종점역 Id를 null로 입력해 지하철 노선을 생성하면
     * Then 노선을 생성할 수 없다
     */
    @DisplayName("지하철 하행 종점역 Id를 null을 입력해 지하철 노선을 생성한다.")
    @Test
    void createLineWithNullTypeDownStationId() {
        // when
        ExtractableResponse<Response> response =
                LineAcceptance.create_line("신분당선", "bg-red-600", 1L, null, 10);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * When 지하철 상행 종점역 Id와 하행 종점역 Id를 같은 값으로 입력해 지하철 노선을 생성하면
     * Then 노선을 생성할 수 없다
     */
    @DisplayName("지하철 상행 종점역 Id와 하행 종점역 Id를 같은 값으로 입력해 지하철 노선을 생성한다.")
    @Test
    void createLineWithSameUpStationIdAndDownStationId() {
        // when
        ExtractableResponse<Response> response =
                LineAcceptance.create_line("신분당선", "bg-red-600", 1L, 1L, 10);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * When 지하철 노선의 거리값을 음수로 입력해 지하철 노선을 생성하면
     * Then 노선을 생성할 수 없다
     */
    @DisplayName("지하철 노선의 거리 값으로 음수을 입력해 지하철 노선을 생성한다.")
    @Test
    void createLineWithNagetiveDistance() {
        // when
        ExtractableResponse<Response> response =
                LineAcceptance.create_line("신분당선", "bg-red-600", 1L, 2L, -1);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }
}
