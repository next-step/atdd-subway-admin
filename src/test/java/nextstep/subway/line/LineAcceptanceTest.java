package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptance.toColorName;
import static nextstep.subway.line.LineAcceptance.toLine;
import static nextstep.subway.line.LineAcceptance.toLineId;
import static nextstep.subway.line.LineAcceptance.toLineName;
import static nextstep.subway.line.LineAcceptance.toLineNames;
import static nextstep.subway.line.LineAcceptance.toList;
import static nextstep.subway.line.LineAcceptance.지하철_노선_목록_조회;
import static nextstep.subway.line.LineAcceptance.지하철_노선_삭제;
import static nextstep.subway.line.LineAcceptance.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptance.지하철_노선_수정;
import static nextstep.subway.line.LineAcceptance.지하철_노선_조회;
import static nextstep.subway.station.StationAcceptance.toStationId;
import static nextstep.subway.station.StationAcceptance.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 인수 테스트")
class LineAcceptanceTest extends AcceptanceTest {

    private Long 대림역_id;
    private Long 신대방역_id;
    private Long 강남역_id;

    private Long 지하철_노선_ID;

    @BeforeEach
    void lineSetUp() {
        대림역_id = toStationId(지하철역_생성("대림역"));
        신대방역_id = toStationId(지하철역_생성("신대방역"));
        강남역_id = toStationId(지하철역_생성("강남역"));
    }

    @AfterEach
    void cleanUp() {
        databaseClean("line", "station", "section");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createStation() {
        return Stream.of(
            dynamicTest("지하철 노선을 생성하면 지하철 노선이 생성된다", () -> {
                // when
                ExtractableResponse<Response> saveResponse = 지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L);

                // then
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("지하철 노선 목록 조회시 생성된 노선을 찾을 수 있다", () -> {
                // then
                ExtractableResponse<Response> getResponse = 지하철_노선_목록_조회();
                List<LineResponse> responses = toList(getResponse);
                LineResponse result = responses.get(0);

                assertAll(
                    () -> assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(responses).hasSize(1),
                    () -> assertThat(result.getName()).isEqualTo("2호선")
                );
            })
        );
    }

    /**
     * given 2개의 지하철 노선을 생성하고
     * when 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLines() {
        return Stream.of(
            dynamicTest("2개의 지하철 노선을 생성한다", () -> {
                // when
                지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L);
                지하철_노선_생성("신분당선", "red", 대림역_id, 강남역_id, 15L);
            }),
            dynamicTest("생성된 노선 목록을 조회하면 2개의 노선을 조회할 수 있다", () -> {
                // when
                ExtractableResponse<Response> response = 지하철_노선_목록_조회();

                // then
                assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(toLineNames(response)).hasSize(2),
                    () -> assertThat(toLineNames(response)).containsAnyOf("2호선", "신분당선")
                );
            })
        );
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 조회하면
     * Then 생성한 지하촐 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLine() {
        return Stream.of(
            dynamicTest("지하철 노선을 생성한다", () -> {
                지하철_노선_ID = toLineId(지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L));
            }),
            dynamicTest("생성된 노선을 조회하면 노선 정보를 응답받는다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(지하철_노선_ID);

                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo("2호선"),
                    () -> assertThat(result.getStations()).hasSize(2)
                );
            })
        );
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * Then 지하철 노선 조회 시 변경된 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @TestFactory
    Stream<DynamicTest> updateLine() {
        return Stream.of(
            dynamicTest("지하철 노선을 생성한다", () -> {
                지하철_노선_ID = toLineId(지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L));
            }),
            dynamicTest("생성된 지하철 노선을 수정한다", () -> {
                ExtractableResponse<Response> updateResponse = 지하철_노선_수정(지하철_노선_ID, "8호선", "skyblue");
                assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            }),
            dynamicTest("지하철 노선 조회시 변경된 노선을 조회한다", () -> {
                ExtractableResponse<Response> getResponse = 지하철_노선_조회(지하철_노선_ID);
                assertThat(toLineName(getResponse)).isEqualTo("8호선");
                assertThat(toColorName(getResponse)).isEqualTo("skyblue");
            })
        );
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @TestFactory
    Stream<DynamicTest> deleteLine() {
        return Stream.of(
            dynamicTest("지하철 노선을 생성한다", () -> {
                지하철_노선_ID = toLineId(지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L));
            }),
            dynamicTest("생성한 지하철 노선을 삭제한다", () -> {
                ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제(지하철_노선_ID);
                assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            })
        );
    }
}
