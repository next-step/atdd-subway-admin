package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    public void createLine_success() {
        // given
        final String lineName = "2호선";
        createLine("강남역", "잠실역", lineName, "bg-green-600", 10);

        // when
        final ExtractableResponse<Response> response = getLines().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames).contains(lineName);
    }


    /**
     * Given 특정한 이름으로 지하철 노선을 만든 뒤
     * When 동일한 이름으로 지하철 노선을 생성하려고 시도하면
     * Then 지하철 노선 생성 요청이 실패한다
     */
    @Test
    @DisplayName("중복되는 이름으로 지하철 노선을 생성할 수 없다.")
    public void createLineWithDuplicateName_badRequest() {
        // given
        final String lineName = "2호선";
        createLine("강남역", "잠실역", lineName, "bg-green-600", 10);

        // when
        final ExtractableResponse<Response> response =
                createLine("강남역", "서초역", lineName, "bg-green-600",25)
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    public void getLines_ok() {
        // given
        final String station1 = "강남역";
        final String station2 = "잠실역";
        final String station3 = "판교역";
        final String lineName1 = "2호선";
        final String lineName2 = "신분당선";
        createLine(station1, station2, lineName1, "bg-green-600", 10);
        createLine(station1, station3, lineName2, "bg-green-600", 10);

        // when
        final ExtractableResponse<Response> response = getLines().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<String> lineNames = getLineNames();
        assertThat(lineNames).contains(lineName1, lineName2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 조회한다.")
    public void getLine_ok() {
        // given
        final ExtractableResponse<Response> createdResponse =
                createLine("강남역", "잠실역", "2호선", "bg-green-600", 10).extract();
        final LineResponse createdLineResponse = createdResponse.as(LineResponse.class);

        // when
        final ExtractableResponse<Response> response = getLine(createdLineResponse.getId()).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse).isEqualTo(createdLineResponse);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    public void editLine_ok() {
        // given
        final String lineName = "2호선";
        final long lineId = createLine("강남역", "잠실역", lineName, "bg-green-600", 10)
                .extract().as(LineResponse.class).getId();

        // when
        final String editStationName = "8호선";
        final String editStationColor = "bg-red-600";
        final LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(editStationName, editStationColor);
        final ExtractableResponse<Response> editResponse = editLine(lineId, lineUpdateRequest).extract();

        // then
        assertThat(editResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // when
        final ExtractableResponse<Response> getResponse = getLine(lineId).extract();

        // then
        final LineResponse lineResponse = getResponse.as(LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(editStationName),
                () -> assertThat(lineResponse.getColor()).isEqualTo(editStationColor)
        );
    }

    /**
     * Given 지하철 노선을 2개 생성하고
     * When 하나의 노선의 이름을 다른 노선의 이름으로 수정하려고 시도하면
     * Then 수정 요청이 실패한다
     */
    @Test
    @DisplayName("중복되는 이름으로 지하철 노선 수정을 시도하면 실패한다.")
    public void editLineWithDuplicateName_badRequest() {
        // given
        final String lineName1 = "2호선";
        final long lineId1 = createLine("강남역", "잠실역", lineName1, "bg-green-600", 10)
                .extract().as(LineResponse.class).getId();

        // given
        final String lineName2 = "5호선";
        final long lineId2 = createLine("올림픽공원역", "천호역", lineName2, "bg-violet-600", 30)
                .extract().as(LineResponse.class).getId();

        // when
        final String editStationColor = "bg-red-600";
        final LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(lineName2, editStationColor);
        final ExtractableResponse<Response> editResponse = editLine(lineId1, lineUpdateRequest).extract();

        // then
        assertThat(editResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    public void deleteLine_noContent() {
        // given
        final String lineName = "2호선";
        final long lineId = createLine("강남역", "잠실역", lineName, "bg-green-600", 10)
                .extract().as(LineResponse.class).getId();

        // when
        deleteLine(lineId);

        // when
        final List<String> lineNames = getLineNames();

        // then
        assertThat(lineNames).doesNotContain(lineName);
    }

    private Response createLineResponse(String upStationName, String downStationName, String name, String color,
                                        int distance) {
        final List<Long> stationIds = Stream.of(upStationName, downStationName)
                .map(StationAcceptanceTest::upsertStationResponse)
                .map(response -> response.as(Station.class))
                .map(Station::getId)
                .collect(Collectors.toList());
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", stationIds.get(0));
        params.put("downStationId", stationIds.get(1));
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines");
    }

    private ValidatableResponse createLine(String upStationName, String downStationName, String name, String color,
                                           int distance) {
        return createLineResponse(upStationName, downStationName, name, color, distance).then().log().all();
    }

    private ValidatableResponse getLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all();
    }

    private List<String> getLineNames() {
        return getLines().extract().jsonPath().getList("name", String.class);
    }

    private ValidatableResponse getLine(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all();
    }

    private ValidatableResponse editLine(long id, LineUpdateRequest lineUpdateRequest) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", lineUpdateRequest.getName());
        params.put("color", lineUpdateRequest.getColor());
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .pathParam("id", id)
                .when().put("/lines/{id}")
                .then().log().all();
    }

    private ValidatableResponse deleteLine(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all();
    }
}
