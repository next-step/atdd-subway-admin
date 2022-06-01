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
import java.util.stream.Stream;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DisplayName("구간 제거 기능")
@Sql(value = {"classpath:truncate_section_table.sql", "classpath:truncate_station_table.sql",
        "classpath:truncate_line_table.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
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
     * Given 노선과 구간이 주어지고 When 구간을 제거하면 Then 해당 역을 찾을 수 없다
     */
    @ParameterizedTest(name = "해당 구간을 제거하면 해당 역을 찾을 수 없다.")
    @MethodSource("구간_추가_파라미터")
    void 구간_삭제후_노선_검색시_해당_구간은_존재하지_않음(SectionRequest 구간_추가_요청, Long 삭제할_역_아이디) {
        // given
        saveStationAndLine();
        ExtractableResponse<Response> 구간_추가_결과 = 구간_추가(생성된_신분당선.getId(), 구간_추가_요청);
        assertThat(구간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> 구간_제거_결과 = 구간_제거(생성된_신분당선.getId(), 삭제할_역_아이디);
        assertThat(구간_제거_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());
        assertAll(
                () -> assertThat(노선_조회_결과.getStations()).hasSize(2),
                () -> assertThat(노선_조회_결과.getStations()).doesNotContain(생성된_양재시민의숲역)
        );
    }

    public static Stream<Arguments> 구간_추가_파라미터() {
        return Stream.of(
                Arguments.of(
                        new SectionRequest(생성된_강남역.getId(), 생성된_양재역.getId(), 5L),
                        생성된_양재역.getId()
                ),
                Arguments.of(
                        new SectionRequest(생성된_양재역.getId(), 생성된_강남역.getId(), 10L),
                        생성된_양재역.getId()
                ),
                Arguments.of(
                        new SectionRequest(생성된_양재시민의숲역.getId(), 생성된_양재역.getId(), 10L),
                        생성된_양재역.getId()
                )
        );
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
}
