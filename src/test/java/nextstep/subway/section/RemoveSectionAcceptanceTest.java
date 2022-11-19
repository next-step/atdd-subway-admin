package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.line.LineNameTestFixture.*;
import static nextstep.subway.section.SectionAcceptanceTestFixture.*;
import static nextstep.subway.station.StationAcceptanceTestFixture.*;
import static nextstep.subway.station.StationNameTestFixture.*;
import static nextstep.subway.utils.JsonPathUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

@DisplayName("지하철역 구간 삭제 관련 기능")
public class RemoveSectionAcceptanceTest extends AcceptanceTest {
    private Integer WANGSIPLI_ID;
    private Integer SEOHYUN_ID;
    private Integer JUKJUN_ID;

    private Integer LINE_BUNDANG_ID_거리_10;

    /**
     * Given 지하철 역과 노선과 여러 구간이 등록되어있고
     * When 상행 종점에 해당하는 역을 제거하면
     * Then 지하철 노선 목록 조회 시 제거한 역을 찾을 수 없다
     */
    @DisplayName("상행 종점을 제거하는 경우")
    @Test
    void removeSection_upStation() {
        // given
        왕십리_서현_죽전_분당선_등록();

        // when
        Map<String, String> params = 구간_삭제_요청_파라미터(WANGSIPLI_ID);
        지하철_구간_삭제(LINE_BUNDANG_ID_거리_10, params);

        // then
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선목록_역목록_이름_추출(response)).contains(SEOHYUN, JUKJUN),
            () -> assertThat(노선목록_역목록_이름_추출(response)).doesNotContain(WANGSIPLI)
        );
    }

    /**
     * Given 지하철 역과 노선과 여러 구간이 등록되어있고
     * When 하행 종점에 해당하는 역을 제거하면
     * Then 지하철 노선 목록 조회 시 제거한 역을 찾을 수 없다
     */
    @DisplayName("하행 종점을 제거하는 경우")
    @Test
    void removeSection_downStation() {
        // given
        왕십리_서현_죽전_분당선_등록();

        // when
        Map<String, String> params = 구간_삭제_요청_파라미터(JUKJUN_ID);
        지하철_구간_삭제(LINE_BUNDANG_ID_거리_10, params);

        // then
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선목록_역목록_이름_추출(response)).contains(WANGSIPLI, SEOHYUN),
            () -> assertThat(노선목록_역목록_이름_추출(response)).doesNotContain(JUKJUN)
        );
    }

    /**
     * Given 지하철 역과 노선과 여러 구간이 등록되어있고
     * When 가운데 역에 해당하는 역을 제거하면
     * Then 지하철 노선 목록 조회 시 제거한 역을 찾을 수 없다
     */
    @DisplayName("가운데 역을 제거하는 경우")
    @Test
    void removeSection_midStation() {
        // given
        왕십리_서현_죽전_분당선_등록();

        // when
        Map<String, String> params = 구간_삭제_요청_파라미터(SEOHYUN_ID);
        지하철_구간_삭제(LINE_BUNDANG_ID_거리_10, params);

        // then
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(노선목록_역목록_이름_추출(response)).contains(WANGSIPLI, JUKJUN),
            () -> assertThat(노선목록_역목록_이름_추출(response)).doesNotContain(SEOHYUN)
        );
    }

    /**
     * Given 지하철 역과 노선과 하나의 구간이 등록되어있고
     * When 해당 노선에 존재하지 않는 역을 제거하면
     * Then 제거할 수 없다.
     */
    @DisplayName("노선에 존재하지 않는 역을 제거하는 경우")
    @Test
    void removeSection_notContains_station() {
        // given
        왕십리_서현_죽전_분당선_등록();
        Integer GANGNAM_ID = ID_추출(지하철역_생성(GANGNAM));

        // when
        Map<String, String> params = 구간_삭제_요청_파라미터(GANGNAM_ID);
        ExtractableResponse<Response> response = 지하철_구간_삭제(LINE_BUNDANG_ID_거리_10, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 역과 노선과 하나의 구간이 등록되어있고
     * When 구간이 하나인 노선에서 해당 역을 제거하면
     * Then 제거할 수 없다.
     */
    @DisplayName("구간이 하나인 노선에서 역을 제거하는 경우")
    @Test
    void removeSection_onlySection_stations() {
        // given
        왕십리_죽전_분당선_등록();

        // when
        Map<String, String> params = 구간_삭제_요청_파라미터(WANGSIPLI_ID);
        ExtractableResponse<Response> response = 지하철_구간_삭제(LINE_BUNDANG_ID_거리_10, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 왕십리_서현_죽전_분당선_등록() {
        왕십리_죽전_분당선_등록();
        분당선_서현_구간_추가();
    }

    private void 왕십리_죽전_분당선_등록() {
        WANGSIPLI_ID = ID_추출(지하철역_생성(WANGSIPLI));
        JUKJUN_ID = ID_추출(지하철역_생성(JUKJUN));
        LINE_BUNDANG_ID_거리_10 = ID_추출(지하철_노선_생성_거리_10(LINE_BUNDANG, WANGSIPLI_ID, JUKJUN_ID));
    }

    private void 분당선_서현_구간_추가() {
        SEOHYUN_ID = ID_추출(지하철역_생성(SEOHYUN));
        Map<String, String> params = 구간_등록_요청_파라미터(WANGSIPLI_ID, SEOHYUN_ID, "5");
        지하철_구간_등록(LINE_BUNDANG_ID_거리_10, params);
    }

    private Integer ID_추출(ExtractableResponse<Response> response) {
        return extractInteger(response, "$.id");
    }

    private List<String> 노선목록_역목록_이름_추출(ExtractableResponse<Response> response) {
        return extractList(response, "$[*].stations[*].name");
    }
}
