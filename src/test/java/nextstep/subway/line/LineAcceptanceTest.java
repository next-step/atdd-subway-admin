package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @TestFactory
    Stream<DynamicTest> 지하철_노선_시나리오() {
        // given
        // 지하철_노선_등록되어_있음
        final LineFixture lineFixture = LineFixture.createOf("2호선", "red lighten-2", 11, 20, 22.2);
        final ExtractableResponse<Response> createResponse = createLine(lineFixture);

        final LineFixture otherLineFixture = LineFixture.createOf("4호선", "red lighten-3", 31, 40, 22.4);
        final ExtractableResponse<Response> otherCreateResponse = createLine(otherLineFixture);

        assertCreateLine(createResponse);
        assertCreateLine(otherCreateResponse);

        return Stream.of(
            DynamicTest.dynamicTest("지하철 노선을 생성한다.", this::지하철_노선을_생성한다),
            DynamicTest.dynamicTest("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.",
                () -> 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다(lineFixture)),
            DynamicTest.dynamicTest("지하철 노선 목록을 조회한다.", () -> 지하철_노선_목록을_조회한다(createResponse, otherCreateResponse)),
            DynamicTest.dynamicTest("지하철 노선을 조회한다.", () -> 지하철_노선을_조회한다(createResponse)),
            DynamicTest.dynamicTest("지하철 노선을 수정한다.", () -> 지하철_노선을_수정한다(createResponse)),
            DynamicTest.dynamicTest("지하철 노선을 제거한다.", () -> 지하철_노선을_제거한다(createResponse))
        );
    }

    void 지하철_노선을_생성한다() {
        //given
        final LineFixture lineFixture = LineFixture.createOf("1호선", "red lighten-1", 1, 10, 22.1);

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> createResponse = createLine(lineFixture);

        // then
        // 지하철_노선_생성됨
        assertCreateLine(createResponse);
    }

    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다(final LineFixture lineFixture) {
        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> createResponse = createLine(lineFixture);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    void 지하철_노선_목록을_조회한다(final ExtractableResponse<Response> createResponse,
        final ExtractableResponse<Response> otherCreateResponse) {
        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        // 지하철_노선_목록_포함됨
        final List<Long> expectedLineIds = Stream.of(createResponse, otherCreateResponse)
            .map(it -> extractId(getLocation(it)))
            .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    void 지하철_노선을_조회한다(final ExtractableResponse<Response> createResponse) {
        // when
        // 지하철_노선_조회_요청
        final String uri = getLocation(createResponse);

        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        final long expectedLineId = extractId(uri);
        final long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    void 지하철_노선을_수정한다(final ExtractableResponse<Response> createResponse) {
        // when
        // 지하철_노선_수정_요청
        final String updateName = "3호선";
        final String updateColor = "red";
        final LineFixture lineFixture = LineFixture.updateOf(updateName, updateColor);

        final String uri = getLocation(createResponse);
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineFixture)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_수정됨
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK),
            () -> assertThat(response.body().jsonPath().getObject(".", LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsExactly(updateName, updateColor)
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void 지하철_노선을_제거한다(final ExtractableResponse<Response> createResponse) {
        // when
        // 지하철_노선_제거_요청
        final String uri = getLocation(createResponse);
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    private ExtractableResponse<Response> createLine(final LineFixture lineFixture) {
        return RestAssured.given().log().all()
            .body(lineFixture)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private void assertCreateLine(ExtractableResponse<Response> response) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED),
            () -> assertThat(getLocation(response)).isNotBlank()
        );
    }

    private long extractId(String location) {
        return Long.parseLong(location.split("/")[2]);
    }

    private String getLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }
}
