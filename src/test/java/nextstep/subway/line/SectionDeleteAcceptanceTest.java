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

@DisplayName("지하철 구간 삭제 관련 기능")
public class SectionDeleteAcceptanceTest extends AcceptanceTest {
    private Integer lineId;
    private Integer oldUpStationId;
    private Integer oldDownStationId;
    private Integer newStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();

        ExtractableResponse<Response> lineCreateResponse = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);
        lineId = lineCreateResponse.jsonPath().get("id");
        oldUpStationId = lineCreateResponse.jsonPath().getList("stations", Station.class).get(0).getId().intValue();
        oldDownStationId = lineCreateResponse.jsonPath().getList("stations", Station.class).get(1).getId().intValue();
        ExtractableResponse<Response> stationCreateResponse = 역_생성_요청_03();
        newStationId = stationCreateResponse.jsonPath().get("id");
    }

    @DisplayName("노선의 구간을 삭제한다 : 중간역 제거")
    @Test
    void deleteStationInMiddle() {
        // given
        지하철_노선에_지하철역_등록_요청(lineId, oldDownStationId, newStationId, 5L);

        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_제거_요청(lineId, oldDownStationId);

        // then
        지하철_노선에_지하철역_제거_성공(response);
    }

    @DisplayName("노선의 구간을 삭제한다 : 가장 왼쪽 역 제거")
    @Test
    void deleteStationInMostLeft() {
        // given
        지하철_노선에_지하철역_등록_요청(lineId, oldDownStationId, newStationId, 5L);

        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_제거_요청(lineId, oldUpStationId);

        // then
        지하철_노선에_지하철역_제거_성공(response);
    }


    @DisplayName("노선의 구간을 삭제한다 : 가장 오른쪽 역 제거")
    @Test
    void deleteStationInMostRight() {
        // given
        지하철_노선에_지하철역_등록_요청(lineId, oldDownStationId, newStationId, 5L);

        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_제거_요청(lineId, newStationId);

        // then
        지하철_노선에_지하철역_제거_성공(response);
    }

    @DisplayName("노선의 구간을 삭제한다 : 구간이 하나인 노선의 역 제거")
    @Test
    void deleteStationInOnlyOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_제거_요청(lineId, oldDownStationId);

        // then
        지하철_노선에_지하철역_제거_실패(response);
    }

    private void 지하철_노선에_지하철역_제거_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_지하철역_제거_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
