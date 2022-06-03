package nextstep.subway.accept;

import static nextstep.subway.accept.LineAcceptanceTest.노선_조회;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.saveStationAndLine;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.구간_추가;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.생성된_강남역;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.생성된_교대역;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.생성된_신분당선;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.생성된_양재시민의숲역;
import static nextstep.subway.accept.SectionAdditionAcceptanceTest.생성된_양재역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DisplayName("구간 제거 기능")
@Sql(value = {"classpath:truncate_section_table.sql", "classpath:truncate_station_table.sql",
        "classpath:truncate_line_table.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SectionSubtractionAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * Given 노선과 구간이 주어지고 When 가운데 구간을 제거하면 Then 해당 역을 찾을 수 없다
     */
    @Test
    @DisplayName("가운데 구간 삭제 후 해당 노선 검색시 해당 구간은 찾을 수 없다.")
    void 가운데_구간_삭제후_노선_검색시_해당_구간은_존재하지_않음() {
        // given
        saveStationAndLine();
        SectionRequest 강남_양재_구간 = new SectionRequest(생성된_강남역.getId(), 생성된_양재역.getId(), 5L);
        구간_추가(생성된_신분당선.getId(), 강남_양재_구간);

        // when
        ExtractableResponse<Response> 구간_제거_결과 = 구간_제거(생성된_신분당선.getId(), 생성된_양재역.getId());
        assertThat(구간_제거_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());
        verifyLineResult(노선_조회_결과, 생성된_양재시민의숲역);
    }

    /**
     * Given 노선과 구간이 주어지고 When 첫번째 구간을 제거하면 Then 해당 역을 찾을 수 없다
     */
    @Test
    @DisplayName("첫번째 구간 삭제 후 해당 노선 검색시 해당 구간은 찾을 수 없다.")
    void 첫번째_구간_삭제후_노선_검색시_해당_구간은_존재하지_않음() {
        // given
        saveStationAndLine();
        SectionRequest 양재_강남_구간 = new SectionRequest(생성된_양재역.getId(), 생성된_강남역.getId(), 10L);
        구간_추가(생성된_신분당선.getId(), 양재_강남_구간);

        // when
        ExtractableResponse<Response> 구간_제거_결과 = 구간_제거(생성된_신분당선.getId(), 생성된_양재역.getId());
        assertThat(구간_제거_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());
        verifyLineResult(노선_조회_결과, 생성된_양재역);
    }

    /**
     * Given 노선과 구간이 주어지고 When 마지막 구간을 제거하면 Then 해당 역을 찾을 수 없다
     */
    @Test
    @DisplayName("마지막 구간 삭제 후 해당 노선 검색시 해당 구간은 찾을 수 없다.")
    void 마지막_구간_삭제후_노선_검색시_해당_구간은_존재하지_않음() {
        // given
        saveStationAndLine();
        SectionRequest 양재시민의숲_양재_구간 = new SectionRequest(생성된_양재시민의숲역.getId(), 생성된_양재역.getId(), 10L);
        구간_추가(생성된_신분당선.getId(), 양재시민의숲_양재_구간);

        // when
        ExtractableResponse<Response> 구간_제거_결과 = 구간_제거(생성된_신분당선.getId(), 생성된_양재역.getId());
        assertThat(구간_제거_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());
        verifyLineResult(노선_조회_결과, 생성된_양재역);
    }

    /**
     * Given 노선과 구간이 주어지고 When 노선에 등록되지 않는 구간을 지우려고 하면 Then 해당 구간은 존재하지 않는다고 에러가 발생한다
     */
    @Test
    @DisplayName("노선에 등록되지 않는 구간을 지우려고 하면 에러가 발생한다.")
    void 없는_구간을_지우려고_하면_에러발생() {
        // given
        saveStationAndLine();

        // when
        ExtractableResponse<Response> 구간_제거_결과 = 구간_제거(생성된_신분당선.getId(), 생성된_교대역.getId());

        // then
        assertThat(구간_제거_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 노선과 구간이 주어지고 When 마지막 남은 구간을 지우려고 하면 Then 해당 구간은 노선에 유일한 구간이여서 지울 수 없다고 에러가 발생한다
     */
    @Test
    @DisplayName("마지막 남은 구간을 지우려고 하면 에러가 발생한다.")
    void 유일한_구간을_지우려고_하면_에러발생() {
        // given
        saveStationAndLine();

        // when
        ExtractableResponse<Response> 구간_제거_결과 = 구간_제거(생성된_신분당선.getId(), 생성된_강남역.getId());

        // then
        assertThat(구간_제거_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간_제거(Long 라인_아이디, Long 삭제할_역_아이디) {
        return RestAssured.given().log().all()
                .param("stationId", 삭제할_역_아이디)
                .when().delete("/lines/{lineId}/sections", 라인_아이디)
                .then().log().all()
                .extract();
    }

    private void verifyLineResult(LineResponse 노선_조회_결과, StationResponse 삭제된_역) {
        assertAll(
                () -> assertThat(노선_조회_결과.getStations()).hasSize(2),
                () -> assertThat(노선_조회_결과.getStations()).doesNotContain(삭제된_역)
        );
    }
}
