package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final LineRequest 분당_라인 = new LineRequest("분당라인", "노란색", 강남역_ID, 역삼역_ID, 10L);
    public static final LineRequest 신분당_라인 = new LineRequest("신분당라인", "빨간색", 강남역_ID, 역삼역_ID, 10L);

    public static final Long 분당_라인_ID = 1L;
    public static final Long 신분당_라인_ID = 2L;

    @DisplayName("지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLine() {
        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("지하철 노선을 생성한다", 라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역}))
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLine2() {
        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("노선을 생성한다", 라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})),
                dynamicTest("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다", () -> {
                    ExtractableResponse<Response> response = 노선_생성_요청(분당_라인);

                    노선_생성_실패_검증(response);
                })
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLines() {
        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("분당라인을 추가한다.", 라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})),
                dynamicTest("신분당라인을 추가한다.", 라인_생성_및_체크(신분당_라인, 신분당_라인_ID, new StationRequest[]{강남역, 역삼역})),
                dynamicTest("지하철 노석 목록을 조회한다", () -> {
                    ExtractableResponse<Response> response = 노선_목록_조회_요청();

                    정상_응답_헤더_검증(response, true);

                    LineResponse[] lineResponses = response.body().as(LineResponse[].class);

                    노선_목록_조회_본문_검증(lineResponses,
                            new Long[]{분당_라인_ID, 신분당_라인_ID},
                            new LineRequest[] {분당_라인, 신분당_라인},
                            new StationRequest[]{강남역, 역삼역});
                })
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLine() {
        return Stream.of(
                dynamicTest("존재하지 않는 노선을 조회한다", 존재하지_않는_라인_확인(분당_라인_ID)),
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("노선을 생성한다", 라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})),
                dynamicTest("노선을 노선을 조회한다", () -> {
                    ExtractableResponse<Response> response = 노선_조회_요청(분당_라인_ID);

                    정상_응답_헤더_검증(response, true);

                    LineResponse lineResponse = response.as(LineResponse.class);

                    노선_조회_본문_검증(lineResponse, 분당_라인_ID, 분당_라인, new StationRequest[]{강남역, 역삼역});
                })
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @TestFactory
    Stream<DynamicTest> updateLine() {
        LineRequest 구_분당선 = new LineRequest("구분당선", "bg-blue-600", 강남역_ID, 역삼역_ID, 10L);

        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("노선을 생성한다", 라인_생성_및_체크(분당_라인, 강남역_ID, new StationRequest[]{강남역, 역삼역})),
                dynamicTest("노선을 수정을 요청한다", () -> {
                    ExtractableResponse<Response> response = 노선_수정_요청(분당_라인_ID, 구_분당선);

                    정상_응답_헤더_검증(response, false);
                }),
                dynamicTest("노선 수정을 확인한다", () -> {
                    ExtractableResponse<Response> response = 노선_조회_요청(분당_라인_ID);

                    정상_응답_헤더_검증(response, true);

                    LineResponse lineResponse = response.as(LineResponse.class);

                    노선_조회_본문_검증(lineResponse, 분당_라인_ID, 구_분당선, new StationRequest[]{강남역, 역삼역});
                })
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @TestFactory
    Stream<DynamicTest> deleteLine() {
        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("노선을 생성한다", 라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})),
                dynamicTest("노선을 삭제을 요청한다", () -> {
                    ExtractableResponse<Response> response = 노선_삭제_요청(분당_라인_ID);

                    노선_데이터_없음_헤더_검증(response);
                }),
                dynamicTest("삭제된 노선을 다시 확인한다", 존재하지_않는_라인_확인(분당_라인_ID))
        );
    }

    private static ExtractableResponse<Response> 노선_생성_요청(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/" + id)
                .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 노선_수정_요청(Long id, LineRequest updateRequest) {
        return RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(ContentType.JSON)
                .when().put("/lines/" + id)
                .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 노선_삭제_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    public static Executable 라인_생성_및_체크(LineRequest lineRequest, Long exceptId, StationRequest[] stations) {
        return () -> {
            ExtractableResponse<Response> response = 노선_생성_요청(lineRequest);

            노선_생성_헤더_검증(response);

            LineResponse lineResponse = response.as(LineResponse.class);
            노선_생성_본문_검증(lineResponse, exceptId, lineRequest, stations);
        };
    }

    private static Executable 존재하지_않는_라인_확인(Long id) {
        return () -> {
            ExtractableResponse<Response> response = 노선_조회_요청(id);

            assertThat(response.statusCode())
                    .isEqualTo(HttpStatus.NO_CONTENT.value());
        };
    }

    private void 정상_응답_헤더_검증(ExtractableResponse<Response> response, boolean requireContentType) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        if (requireContentType) {
            assertThat(response.header(HttpHeaders.CONTENT_TYPE)).isIn(ContentType.JSON.getContentTypeStrings());
        }
    }

    private static void 노선_생성_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE)).isIn(ContentType.JSON.getContentTypeStrings());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
    }

    private static void 노선_생성_본문_검증(
            LineResponse lineResponse,
            Long exceptId,
            LineRequest requestedLine,
            StationRequest[] stations
    ) {
        노선_조회_본문_검증(lineResponse, exceptId, requestedLine, stations);
    }

    private static void 노선_생성_실패_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 노선_목록_조회_본문_검증(
            LineResponse[] lineResponses,
            Long exceptedIds[],
            LineRequest[] lineRequests,
            StationRequest[] stations
    ) {
        assertThat(lineResponses).hasSize(exceptedIds.length);

        for (int i = 0; i < lineResponses.length; i++) {
            노선_조회_본문_검증(
                    lineResponses[i],
                    exceptedIds[i],
                    lineRequests[i],
                    stations
            );
        }
    }

    private static void 노선_조회_본문_검증(
            LineResponse lineResponse,
            Long exceptedId,
            LineRequest lineRequest,
            StationRequest[] stations
    ) {
        assertThat(lineResponse.getId())
                .isEqualTo(exceptedId);

        assertThat(lineResponse.getName())
                .isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor())
                .isEqualTo(lineRequest.getColor());

        assertThat(lineResponse.getCreatedDate())
                .isNotNull();
        assertThat(lineResponse.getModifiedDate())
                .isNotNull();

        List<Long> stationIdsInLine = lineResponse.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<String> stationNamesInLine = lineResponse.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNamesInLine)
                .containsExactlyElementsOf(
                        Arrays.stream(stations)
                        .map(StationRequest::getName)
                        .collect(Collectors.toList())
                );
        assertThat(stationIdsInLine)
                .containsExactlyInAnyOrder(lineRequest.getUpStationId(), lineRequest.getDownStationId());
    }

    private static void 노선_데이터_없음_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
