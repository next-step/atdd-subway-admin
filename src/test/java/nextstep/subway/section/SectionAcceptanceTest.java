package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptance.toLine;
import static nextstep.subway.line.LineAcceptance.toLineId;
import static nextstep.subway.line.LineAcceptance.toLineStationNames;
import static nextstep.subway.line.LineAcceptance.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptance.지하철_노선_조회;
import static nextstep.subway.section.SectionAcceptance.ID가_상행역인_노선;
import static nextstep.subway.section.SectionAcceptance.지하철_구간_등록;
import static nextstep.subway.station.StationAcceptance.toStationId;
import static nextstep.subway.station.StationAcceptance.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long 신도림역_id;
    private Long 대림역_id;
    private Long 신대방역_id;
    private Long 신림역_id;
    private Long 강남역_id;
    private Long 노선_id;

    @BeforeEach
    void sectionSetUp() {
        신도림역_id = toStationId(지하철역_생성("신도림역"));
        대림역_id = toStationId(지하철역_생성("대림역"));
        신대방역_id = toStationId(지하철역_생성("신대방역"));
        신림역_id = toStationId(지하철역_생성("신림역"));
        강남역_id = toStationId(지하철역_생성("강남역"));
        노선_id = toLineId(지하철_노선_생성("2호선", "green", 대림역_id, 강남역_id, 20L));
    }

    @AfterEach
    void cleanUp() {
        databaseClean("section", "line", "station");
    }

    @DisplayName("역과 역 사이에 새로운 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> createSection() {
        return Stream.of(
            dynamicTest("역 사이에 새로운 역을 등록한다" , () -> {
                Map<String, Object> params = new HashMap<>();
                params.put("upStationId", 대림역_id);
                params.put("downStationId", 신대방역_id);
                params.put("distance", 7L);

                ExtractableResponse<Response> saveResponse = 지하철_구간_등록(노선_id, params);
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("노선을 조회하면 생성된 구간이 같이 조회되어야 한다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(노선_id);
                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(result.getSections()).hasSize(2),
                    () -> assertThat(ID가_상행역인_노선(대림역_id, result.getSections()).getDistance()).isEqualTo(7L),
                    () -> assertThat(ID가_상행역인_노선(신대방역_id, result.getSections()).getDistance()).isEqualTo(13L),
                    () -> assertThat(toLineStationNames(result)).contains("대림역", "신대방역", "강남역")
                );
            })
        );
    }

    @DisplayName("새로운 역을 상행 종점에 등록한다.")
    @TestFactory
    Stream<DynamicTest> createSection2() {
        return Stream.of(
            dynamicTest("새로운 역을 상행 종점으로 등록한다" , () -> {
                Map<String, Object> params = new HashMap<>();
                params.put("upStationId", 신도림역_id);
                params.put("downStationId", 대림역_id);
                params.put("distance", 10L);

                ExtractableResponse<Response> saveResponse = 지하철_구간_등록(노선_id, params);
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("노선을 조회하면 상행 종점으로 생성된 구간이 조회되어야 한다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(노선_id);
                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(result.getSections()).hasSize(2),
                    () -> assertThat(ID가_상행역인_노선(신도림역_id, result.getSections()).getDistance()).isEqualTo(10L),
                    () -> assertThat(ID가_상행역인_노선(대림역_id, result.getSections()).getDistance()).isEqualTo(20L),
                    () -> assertThat(toLineStationNames(result)).contains("신도림역", "대림역", "강남역")
                );
            })
        );
    }

    @DisplayName("새로운 역을 하행 종점에 등록한다.")
    @TestFactory
    Stream<DynamicTest> createSection3() {
        return Stream.of(
            dynamicTest("새로운 역을 하행 종점으로 등록한다" , () -> {
                Map<String, Object> params = new HashMap<>();
                params.put("upStationId", 강남역_id);
                params.put("downStationId", 신림역_id);
                params.put("distance", 10L);

                ExtractableResponse<Response> saveResponse = 지하철_구간_등록(노선_id, params);
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("노선을 조회하면 하행 종점으로 생성된 구간이 조회되어야 한다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(노선_id);
                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(result.getSections()).hasSize(2),
                    () -> assertThat(ID가_상행역인_노선(대림역_id, result.getSections()).getDistance()).isEqualTo(20L),
                    () -> assertThat(ID가_상행역인_노선(강남역_id, result.getSections()).getDistance()).isEqualTo(10L),
                    () -> assertThat(toLineStationNames(result)).contains("대림역", "강남역", "신림역")
                );
            })
        );
    }
}
