package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse gyodaeStation;
    private StationResponse gangnamStation;
    private StationResponse yeoksamStation;
    private StationResponse seolleungStation;
    private ExtractableResponse<Response> secondLineResponse;

    @BeforeEach
    void beforeEach() {
        gyodaeStation = 지하철_역_생성("교대역");
        gangnamStation = 지하철_역_생성("강남역");
        yeoksamStation = 지하철_역_생성("역삼역");
        seolleungStation = 지하철_역_생성("선릉역");

        secondLineResponse = 지하철_노선_등록되어_있음(
            "2호선", "blue",
            gangnamStation.getId(), seolleungStation.getId(), 10
        );
    }

    @Test
    @DisplayName("노선 가장 앞에 구간을 추가한다.")
    void addSection_firstSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            givenLineId(), gyodaeStation.getId(), gangnamStation.getId(), Integer.MAX_VALUE);

        // then
        지하철_노선에_지하철역_등록됨(response, gyodaeStation, gangnamStation, seolleungStation);
    }

    @Test
    @DisplayName("상행선으로 노선 사이에 구간을 추가한다.")
    void addSection_inBetweenByUpStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            givenLineId(), gangnamStation.getId(), yeoksamStation.getId(), 5);

        // then
        지하철_노선에_지하철역_등록됨(response, gangnamStation, yeoksamStation, seolleungStation);
    }

    @Test
    @DisplayName("하행선으로 사이에 구간을 추가한다.")
    void addSection_inBetweenByDownStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            givenLineId(), yeoksamStation.getId(), seolleungStation.getId(), 5);

        // then
        지하철_노선에_지하철역_등록됨(response, gangnamStation, yeoksamStation, seolleungStation);
    }


    @Test
    @DisplayName("존재하지 않는 노선에 구간을 등록한다.")
    void addSection_notExistsLine_404() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            Long.MIN_VALUE, gyodaeStation.getId(), gangnamStation.getId(), Integer.MAX_VALUE);

        // then
        지하철_노선_못찾음(response);
    }

    @Test
    @DisplayName("사이 거리가 더 크면 등록이 불가능")
    void addSection_greaterThanBetweenDistance_400() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            givenLineId(), yeoksamStation.getId(), seolleungStation.getId(), Integer.MAX_VALUE);

        //then
        지하철_노선_구간_생성_실패됨(response);
    }

    @Test
    @DisplayName("추가하려는 구간의 역들이 모두 존재한다.")
    void addSection_upAndDownStationExistsStation_400() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            givenLineId(), gangnamStation.getId(), seolleungStation.getId(), 10);

        //then
        지하철_노선_구간_생성_실패됨(response);
    }

    @Test
    @DisplayName("구간의 역들이 존재하지 않으면 생성할 수 없다.")
    void addSection_notExistsAnyStation_400() {
        //given
        StationResponse guro = 지하철_역_생성("구로");
        StationResponse gaebong = 지하철_역_생성("개봉");

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            givenLineId(), guro.getId(), gaebong.getId(), Integer.MAX_VALUE);

        //then
        지하철_노선_구간_생성_실패됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color,
        long upStationId, long downStationId, int distance) {
        return RestAssured.given()
            .body(new LineCreateRequest(name, color,
                new SectionRequest(upStationId, downStationId, distance)))
            .contentType(ContentType.JSON)
            .post("/lines")
            .then()
            .extract();
    }

    private StationResponse 지하철_역_생성(String name) {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(new StationRequest(name))
            .when()
            .post("/stations")
            .then()
            .extract()
            .as(StationResponse.class);
    }

    private void 지하철_노선_못찾음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId,
        Long upStationId, Long downStationId, Integer distance) {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(new SectionRequest(upStationId, downStationId, distance))
            .when()
            .post("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_구간_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Long givenLineId() {
        return secondLineResponse.as(LineResponse.class)
            .getId();
    }

    private void 지하철_노선에_지하철역_등록됨(
        ExtractableResponse<Response> response, StationResponse... expectedStations) {
        LineResponse secondLine = 지하철_노선_조회_요청(secondLineResponse).as(LineResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(secondLine.getStations())
                .hasSize(3)
                .extracting(StationResponse::getId)
                .containsExactly(
                    Arrays.stream(expectedStations)
                        .map(StationResponse::getId)
                        .toArray(Long[]::new)
                )
        );
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(
        ExtractableResponse<Response> createdResponse) {
        return RestAssured.given().log().all()
            .when()
            .get(createdResponse.header("Location"))
            .then().log().all()
            .extract();
    }
}
