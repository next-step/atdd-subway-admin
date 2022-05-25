package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AssertUtils;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.station.StationRestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    private long lineId = -1;

    @DisplayName("지하철노선 인수 테스트")
    @TestFactory
    Stream<DynamicNode> dynamicCreateLine() {
        long 강남역_id = toStationId(StationRestAssured.createStation("강남역"));
        long 청량리역_id = toStationId(StationRestAssured.createStation("청량리역"));
        long 정자역_id = toStationId(StationRestAssured.createStation("정자역"));

        return Stream.of(
                dynamicTest("지하철노선 생성", () -> {
                    ExtractableResponse<Response> response =
                            LineRestAssured.callCreateLine("신분당선", "bg-red-600", 강남역_id, 정자역_id, 10L);
                    lineId = toLineId(response);

                    assertAll(
                            () -> AssertUtils.assertStatusCode(response, HttpStatus.CREATED),
                            () -> {
                                String locationHeaderValue = String.format("/lines/%d", lineId);
                                assertThat(response.header("Location")).isEqualTo(locationHeaderValue);
                            },
                            () -> assertThat(toLineNames(LineRestAssured.callGetLines())).containsAnyOf("신분당선")
                    );
                }),
                dynamicTest("중복되는 지하철노선 생성", () -> {
                    ExtractableResponse<Response> response =
                            LineRestAssured.callCreateLine("신분당선", "bg-red-600", 강남역_id, 정자역_id, 10L);

                    AssertUtils.assertStatusCode(response, HttpStatus.BAD_REQUEST);
                }),
                dynamicTest("지하철노선 목록 조회", () -> {
                    LineRestAssured.callCreateLine("분당선", "bg-yellow-600", 청량리역_id, 정자역_id, 10L);

                    ExtractableResponse<Response> response = LineRestAssured.callGetLines();

                    assertAll(
                            () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                            () -> assertThat(toLineNames(response)).containsAnyOf("신분당선", "분당선")
                    );
                }),
                dynamicTest("지하철노선 조회", () -> {
                    ExtractableResponse<Response> response = callGetLine(lineId);

                    assertAll(
                            () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                            () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선")
                    );
                }),
                dynamicTest("존재하지 않는 지하철노선 조회", () -> {
                    ExtractableResponse<Response> response = callGetLine(lineId + 1);

                    AssertUtils.assertStatusCode(response, HttpStatus.BAD_REQUEST);
                }),
                dynamicTest("지하철노선 수정", () -> {
                    ExtractableResponse<Response> response = callUpdateLine(lineId, "아무개선", "bg-blue-600");

                    AssertUtils.assertStatusCode(response, HttpStatus.OK);
                    ExtractableResponse<Response> getLineResponse = callGetLine(lineId);
                    JsonPath jsonPath = getLineResponse.body().jsonPath();
                    assertAll(
                            () -> assertThat(jsonPath.getString("name")).isEqualTo("아무개선"),
                            () -> assertThat(jsonPath.getString("color")).isEqualTo("bg-blue-600")
                    );
                }),
                dynamicTest("지하철노선 삭제", () -> {
                    callDeleteLine(lineId);

                    ExtractableResponse<Response> response = LineRestAssured.callGetLines();
                    assertAll(
                            () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                            () -> assertThat(toLineNames(response)).doesNotContain("신분당선")
                    );
                })
        );
    }

    private ExtractableResponse<Response> callGetLine(long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> callUpdateLine(long lineId, String updateName, String updateColor) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", updateName);
        params.put("color", updateColor);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> callDeleteLine(long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private long toStationId(ExtractableResponse<Response> response) {
        return AssertUtils.toId(response);
    }

    private long toLineId(ExtractableResponse<Response> response) {
        return AssertUtils.toId(response);
    }

    private List<String> toLineNames(ExtractableResponse<Response> response) {
        return response.body()
                       .jsonPath()
                       .getList("name", String.class);
    }
}
