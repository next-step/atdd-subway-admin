package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineRestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Integer lineId;
    private Integer oldUpStationId;
    private Integer oldDownStationId;
    private Integer newStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();

        ExtractableResponse<Response> lineCreateResponse = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);
        ExtractableResponse<Response> stationCreateResponse = 역_생성_요청_03();

        lineId = lineCreateResponse.jsonPath().get("id");
        oldUpStationId = lineCreateResponse.jsonPath().getList("stations", Station.class).get(0).getId().intValue();
        oldDownStationId = lineCreateResponse.jsonPath().getList("stations", Station.class).get(1).getId().intValue();
        newStationId = stationCreateResponse.jsonPath().get("id");
    }

    @DisplayName("노선에 구간을 등록한다 : 상행역 동일, 하행역 중간")
    @Test
    void addSectionUpStationIsSameAndDownStationInMiddle() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, oldUpStationId, newStationId, 3L);

        // then
        지하철_노선에_지하철역_등록_성공(response);
    }

    @DisplayName("노선에 구간을 등록한다 : 상행역 동일, 하행역 중간. 거리 초과")
    @Test
    void addSectionUpStationIsSameAndDownStationInMiddleButDistanceIsOver() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, oldUpStationId, newStationId, 11L);

        // then
        지하철_노선에_지하철역_등록_실패(response);
    }

    @DisplayName("노선에 구간을 등록한다 : 하행역 동일, 상행역 중간")
    @Test
    void addSectionDownStationIsSameAndUpStationInMiddle() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, newStationId, oldUpStationId, 3L);

        // then
        지하철_노선에_지하철역_등록_성공(response);
    }

    @DisplayName("노선에 구간을 등록한다 : 하행역 동일, 상행역 중간. 거리 초과")
    @Test
    void addSectionDownStationIsSameAndUpStationInMiddleButDistanceIsOver() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, newStationId, oldDownStationId, 11L);

        // then
        지하철_노선에_지하철역_등록_실패(response);
    }

    @DisplayName("노선에 구간을 등록한다 : 하행역을 기존 구간의 상행역으로")
    @Test
    void addSectionDownStationIsSameAtTheEnd() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, newStationId, oldUpStationId, 3L);

        // then
        지하철_노선에_지하철역_등록_성공(response);
    }

    @DisplayName("노선에 구간을 등록한다 : 상행역을 기존 구간의 하행역으로")
    @Test
    void addSectionUpStationIsSameAtTheEnd() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, oldDownStationId, newStationId, 3L);

        // then
        지하철_노선에_지하철역_등록_성공(response);
    }

    private void 지하철_노선에_지하철역_등록_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선에_지하철역_등록_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
