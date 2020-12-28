package nextstep.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.StationAcceptanceTestRequest;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long lineUpStationId;
    private Long lineDownStationId;
    private Long lineNewStationId;
    private String lineUri;


    @BeforeEach
    public void setUp() {
        super.setUp();
        // 지하철역_등록되어_있음
        // 지하철_노선_등록되어_있음
        lineUpStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("문래역");
        lineDownStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("잠실역");
        lineNewStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("사당역");
        lineUri = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green", lineUpStationId, lineDownStationId, 10);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
    }
}
