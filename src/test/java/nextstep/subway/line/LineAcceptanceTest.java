package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTestFixture.*;
import static nextstep.subway.utils.JsonPathUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.JsonPathUtils;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    public static final String LINE_2 = "2호선";
    public static final String LINE_BUNDANG = "분당선";

    private Integer GANGNAM_ID;
    private Integer WANGSIPLI_ID;
    private Integer JUKJUN_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        GANGNAM_ID = ID_추출(지하철역_생성(GANGNAM));
        WANGSIPLI_ID = ID_추출(지하철역_생성(WANGSIPLI));
        JUKJUN_ID = ID_추출(지하철역_생성(JUKJUN));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createStationLine() {
        // when
        지하철_노선_생성(LINE_2, GANGNAM_ID, WANGSIPLI_ID);

        // then
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선목록_이름_추출(response)).contains(LINE_2),
            () -> assertThat(노선목록_역목록_이름_추출(response)).contains(GANGNAM, WANGSIPLI)
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getStationLines() {
        // given
        지하철_노선_생성(LINE_2, GANGNAM_ID, WANGSIPLI_ID);
        지하철_노선_생성(LINE_BUNDANG, WANGSIPLI_ID, JUKJUN_ID);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선목록_이름_추출(response)).contains(LINE_2, LINE_BUNDANG),
            () -> assertThat(노선목록_역목록_이름_추출(response)).contains(GANGNAM, WANGSIPLI, JUKJUN)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getStationLine() {
        // given
        Integer id = JsonPathUtils.extractInteger(지하철_노선_생성(LINE_2, GANGNAM_ID, WANGSIPLI_ID), "$.id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(id);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선_이름_추출(response)).isEqualTo(LINE_2),
            () -> assertThat(역목록_이름_추출(response)).contains(GANGNAM, WANGSIPLI)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateStationLine() {
        // given
        Integer id = JsonPathUtils.extractInteger(지하철_노선_생성(LINE_2, GANGNAM_ID, WANGSIPLI_ID), "$.id");

        // when
        String CHANGED_NAME = "8호선";
        String CHANGED_COLOR = "bg-red-600";
        ExtractableResponse<Response> response = 지하철_노선_수정(id, CHANGED_NAME, CHANGED_COLOR);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선_이름_추출(response)).isEqualTo(CHANGED_NAME),
            () -> assertThat(노선_색_추출(response)).isEqualTo(CHANGED_COLOR)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteStationLine() {
        // given
        Integer id = JsonPathUtils.extractInteger(지하철_노선_생성(LINE_2, GANGNAM_ID, WANGSIPLI_ID), "$.id");

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestFixture.지하철_노선_삭제(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(노선목록_이름_추출(지하철_노선_목록_조회())).doesNotContain(LINE_2);
    }

    private Integer ID_추출(ExtractableResponse<Response> response) {
        return extractInteger(response, "$.id");
    }

    private List<String> 역목록_이름_추출(ExtractableResponse<Response> response) {
        return extractList(response, "$.stations[*].name");
    }

    private String 노선_이름_추출(ExtractableResponse<Response> response) {
        return extractString(response, "$.name");
    }

    private String 노선_색_추출(ExtractableResponse<Response> response) {
        return extractString(response, "$.color");
    }

    private List<String> 노선목록_이름_추출(ExtractableResponse<Response> response) {
        return extractList(response, "$[*].name");
    }

    private List<String> 노선목록_역목록_이름_추출(ExtractableResponse<Response> response) {
        return extractList(response, "$[*].stations[*].name");
    }
}
