package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final LineRequest 분당_라인 = new LineRequest("분당라인", "노란색");
    private static final LineRequest 신분당_라인 = new LineRequest("신분당라인", "빨간색");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLineRequest(분당_라인);

        // then
        노선_생성_헤더_검증(response);

        LineResponse lineResponse = response.as(LineResponse.class);

        노선_생성_본문_검증(lineResponse, 1L, 분당_라인);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLine2() {
        return Stream.of(
                dynamicTest("노선을 생성한다", 간단한_라인_생성_및_체크(분당_라인, 1L)),
                dynamicTest("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다", () -> {
                    ExtractableResponse<Response> response = createLineRequest(분당_라인);

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLines() {
        return Stream.of(
                dynamicTest("분당라인을 추가한다.", 간단한_라인_생성_및_체크(분당_라인, 1L)),
                dynamicTest("신분당라인을 추가한다.", 간단한_라인_생성_및_체크(신분당_라인, 2L)),
                dynamicTest("지하철 노석 목록을 조회한다", () -> {
                    ExtractableResponse<Response> response = getLinesRequest();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(response.header(HttpHeaders.CONTENT_TYPE)).isIn(ContentType.JSON.getContentTypeStrings());

                    LineResponse[] lineResponses = response.body().as(LineResponse[].class);

                    assertThat(lineResponses).hasSize(2);

                    assertThat(lineResponses)
                            .extracting(item -> item.getId())
                            .containsExactly(1L, 2L);

                    assertThat(lineResponses)
                            .extracting(item -> item.getCreatedDate())
                            .isNotEmpty();
                    assertThat(lineResponses)
                            .extracting(item -> item.getModifiedDate())
                            .isNotEmpty();

                    assertThat(lineResponses)
                            .extracting(item -> item.getName())
                            .containsExactlyInAnyOrder(
                                    분당_라인.getName(),
                                    신분당_라인.getName()
                            );
                    assertThat(lineResponses)
                            .extracting(item -> item.getColor())
                            .containsExactlyInAnyOrder(
                                    분당_라인.getColor(),
                                    신분당_라인.getColor()
                            );
                })
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLine() {
        return Stream.of(
                dynamicTest("존재하지 않는 노선을 조회한다", 존재하지_않는_라인_확인(1L)),
                dynamicTest("노선을 생성한다", 간단한_라인_생성_및_체크(분당_라인, 1L)),
                dynamicTest("노선을 노선을 조회한다", () -> {
                    ExtractableResponse<Response> response = getLineRequest(1L);

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(response.header(HttpHeaders.CONTENT_TYPE))
                            .isIn(ContentType.JSON.getContentTypeStrings());

                    LineResponse lineResponse = response.as(LineResponse.class);

                    assertThat(lineResponse.getName())
                            .isEqualTo(분당_라인.getName());
                    assertThat(lineResponse.getColor())
                            .isEqualTo(분당_라인.getColor());
                })
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @TestFactory
    Stream<DynamicTest> updateLine() {
        LineRequest updateRequest = new LineRequest("구분당선", "bg-blue-600");

        return Stream.of(
                dynamicTest("노선을 생성한다", 간단한_라인_생성_및_체크(분당_라인, 1L)),
                dynamicTest("노선을 수정을 요청한다", () -> {
                    ExtractableResponse<Response> response = RestAssured
                            .given().log().all()
                            .body(updateRequest)
                            .contentType(ContentType.JSON)
                            .when().put("/lines/1")
                            .then().log().all().extract();

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("노선 수정을 확인한다", () -> {
                    ExtractableResponse<Response> response = getLineRequest(1L);

                    assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.OK.value());
                    assertThat(response.header(HttpHeaders.CONTENT_TYPE))
                            .isIn(ContentType.JSON.getContentTypeStrings());

                    LineResponse lineResponse = response.as(LineResponse.class);

                    assertThat(lineResponse.getName())
                            .isEqualTo(updateRequest.getName());
                    assertThat(lineResponse.getColor())
                            .isEqualTo(updateRequest.getColor());
                })
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @TestFactory
    Stream<DynamicTest> deleteLine() {
        return Stream.of(
                dynamicTest("노선을 생성한다", 간단한_라인_생성_및_체크(분당_라인, 1L)),
                dynamicTest("노선을 삭제을 요청한다", () -> {
                    ExtractableResponse<Response> response = RestAssured
                            .given().log().all()
                            .when().delete("/lines/1")
                            .then().log().all().extract();

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),
                dynamicTest("삭제된 노선을 다시 확인한다", 존재하지_않는_라인_확인(1L))
        );
    }

    private List<ExtractableResponse<Response>> createLineRequests(LineRequest... requests) {
        List<ExtractableResponse<Response>> results = new ArrayList<>();
        for (LineRequest request : requests) {
            results.add(createLineRequest(request));
        }

        return results;
    }

    private ExtractableResponse<Response> createLineRequest(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLinesRequest() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> getLineRequest(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/" + id)
                .then().log().all().extract();
    }

    private Executable 간단한_라인_생성_및_체크(LineRequest lineRequest, Long exceptId) {
        return () -> {
            ExtractableResponse<Response> response = createLineRequest(lineRequest);

            노선_생성_헤더_검증(response);

            LineResponse lineResponse = response.as(LineResponse.class);
            노선_생성_본문_검증(lineResponse, exceptId, lineRequest);
        };
    }

    private Executable 존재하지_않는_라인_확인(Long id) {
        return () -> {
            ExtractableResponse<Response> response = getLineRequest(id);

            assertThat(response.statusCode())
                    .isEqualTo(HttpStatus.NO_CONTENT.value());
        };
    }


    private void 노선_생성_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE)).isIn(ContentType.JSON.getContentTypeStrings());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
    }

    private void 노선_생성_본문_검증(
            LineResponse lineResponse,
            Long exceptId,
            LineRequest requestedLine
    ) {
        assertThat(lineResponse.getId()).isEqualTo(exceptId);
        assertThat(lineResponse.getName()).isEqualTo(requestedLine.getName());
        assertThat(lineResponse.getColor()).isEqualTo(requestedLine.getColor());
        assertThat(lineResponse.getCreatedDate()).isNotNull();
        assertThat(lineResponse.getModifiedDate()).isNotNull();
    }
}
