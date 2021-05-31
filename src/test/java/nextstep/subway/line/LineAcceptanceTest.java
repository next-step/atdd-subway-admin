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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE)).isIn(ContentType.JSON.getContentTypeStrings());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();

        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo(분당_라인.getName());
        assertThat(lineResponse.getColor()).isEqualTo(분당_라인.getColor());
        assertThat(lineResponse.getCreatedDate()).isNotNull();
        assertThat(lineResponse.getModifiedDate()).isNotNull();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLine2() {
        return Stream.of(
                dynamicTest("노선을 생성한다", 간단한_분당라인_생성_및_체크()),
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
                dynamicTest("지하철 노선을 여러개 추가한다", () -> {
                    List<ExtractableResponse<Response>> lineRequests = createLineRequests(분당_라인, 신분당_라인);

                    assertThat(lineRequests)
                            .extracting(item -> item.statusCode())
                            .containsOnly(HttpStatus.CREATED.value());
                }),
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
                dynamicTest("존재하지 않는 노선을 조회한다", () -> {
                    ExtractableResponse<Response> response = getLineRequest(1L);

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),
                dynamicTest("노선을 생성한다", 간단한_분당라인_생성_및_체크()),
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
                dynamicTest("노선을 생성한다", 간단한_분당라인_생성_및_체크()),
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
                dynamicTest("노선을 생성한다", 간단한_분당라인_생성_및_체크()),
                dynamicTest("노선을 삭제을 요청한다", () -> {
                    ExtractableResponse<Response> response = RestAssured
                            .given().log().all()
                            .when().delete("/lines/1")
                            .then().log().all().extract();

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),
                dynamicTest("노선을 다시 조회한다", () -> {
                    ExtractableResponse<Response> response = getLineRequest(1L);

                    assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.NO_CONTENT.value());
                })
        );
    }

    private List<ExtractableResponse<Response>> createLineRequests(LineRequest ...requests) {
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

    private Executable 간단한_분당라인_생성_및_체크() {
        return () -> {
            ExtractableResponse<Response> response = createLineRequest(분당_라인);

            assertThat(response.statusCode())
                    .isEqualTo(HttpStatus.CREATED.value());
        };
    }
}
