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

    private StationResponse gangnamStation;
    private StationResponse yeoksamStation;
    private StationResponse seolleungStation;
    private LineResponse secondLine;

    @BeforeEach
    void beforeEach() {
        gangnamStation = 지하철_역_생성("강남역");
        yeoksamStation = 지하철_역_생성("역삼역");
        seolleungStation = 지하철_역_생성("선릉역");

        secondLine = 지하철_노선_등록되어_있음(
            "2호선", "blue",
            gangnamStation.getId(), seolleungStation.getId(), Integer.MAX_VALUE
        ).as(LineResponse.class);
    }

    @Test
    @DisplayName("상행 종점 구간을 추가한다.")
    void addSection_firstSection() {
        //given
        StationResponse gyodaeStation = 지하철_역_생성("교대역");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), gyodaeStation.getId(), gangnamStation.getId(), Integer.MAX_VALUE);

        // then
        지하철_노선에_지하철역_등록됨(response, gyodaeStation, gangnamStation, seolleungStation);
    }

    @Test
    @DisplayName("하행 종점 구간을 추가한다.")
    void addSection_lastSection() {
        //given
        StationResponse samseongStation = 지하철_역_생성("삼성역");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), seolleungStation.getId(), samseongStation.getId(),
            Integer.MAX_VALUE);

        // then
        지하철_노선에_지하철역_등록됨(response, gangnamStation, seolleungStation, samseongStation);
    }

    @Test
    @DisplayName("상행선으로 노선 사이에 구간을 추가한다.")
    void addSection_inBetweenByUpStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), gangnamStation.getId(), yeoksamStation.getId(), 1);

        // then
        지하철_노선에_지하철역_등록됨(response, gangnamStation, yeoksamStation, seolleungStation);
    }

    @Test
    @DisplayName("하행선으로 노선 사이에 구간을 추가한다.")
    void addSection_inBetweenByDownStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), yeoksamStation.getId(), seolleungStation.getId(), 1);

        // then
        지하철_노선에_지하철역_등록됨(response, gangnamStation, yeoksamStation, seolleungStation);
    }


    @Test
    @DisplayName("존재하지 않는 노선에 구간을 등록한다.")
    void addSection_notExistsLine_404() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            Long.MIN_VALUE, gangnamStation.getId(), yeoksamStation.getId(), Integer.MAX_VALUE);

        // then
        지하철_노선_못찾음(response);
    }

    @Test
    @DisplayName("사이 거리가 더 크면 등록이 불가능")
    void addSection_greaterThanBetweenDistance_400() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), yeoksamStation.getId(), seolleungStation.getId(),
            Integer.MAX_VALUE);

        //then
        지하철_노선_구간_생성_실패됨(response);
    }

    @Test
    @DisplayName("추가하려는 구간의 역들이 모두 존재한다.")
    void addSection_upAndDownStationExistsStation_400() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), gangnamStation.getId(), seolleungStation.getId(), 1);

        //then
        지하철_노선_구간_생성_실패됨(response);
    }

    @Test
    @DisplayName("추가하려는 구간의 역들이 노선에 존재하지 않으면 생성할 수 없다.")
    void addSection_notExistsAnyStation_400() {
        //given
        StationResponse guro = 지하철_역_생성("구로");
        StationResponse gaebong = 지하철_역_생성("개봉");

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(
            secondLine.getId(), guro.getId(), gaebong.getId(), 1);

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

    private void 지하철_노선에_지하철역_등록됨(
        ExtractableResponse<Response> response, StationResponse... expectedStations) {
        LineResponse secondLine = 지하철_노선_조회_요청(this.secondLine).as(LineResponse.class);
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

    private ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse line) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", line.getId())
            .then().log().all()
            .extract();
    }
}
