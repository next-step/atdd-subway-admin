package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceStep.등록된_지하철노선;
import static nextstep.subway.line.SectionAcceptanceStep.지하철구간_생성_응답상태_201_검증;
import static nextstep.subway.line.SectionAcceptanceStep.지하철노선에_구간_등록_요청;
import static nextstep.subway.station.StationAcceptanceStep.등록된_지하철역;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long lineId;
    private Long stationId1;
    private Long registeredStationId1;
    private Long registeredStationId2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> createLineResponse = 등록된_지하철노선("2호선", "indigo darken-4", 10,"신촌역", "을지로역");
        LineResponse lineResponse = createLineResponse.as(LineResponse.class);
        lineId = lineResponse.getId();

        List<StationResponse> stations = lineResponse.getStations();
        registeredStationId1 = stations.get(0).getId();
        registeredStationId2 = stations.get(1).getId();


        ExtractableResponse<Response> createdStationResponse1 = 등록된_지하철역("용산역");
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 응답상태 201을 반환한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, registeredStationId1, stationId1, 3);

        // then
        지하철구간_생성_응답상태_201_검증(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 응답상태 201을 반환한다.")
    @Test
    void createSectionToAscentEndpoint() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, stationId1, registeredStationId1, 3);

        // then
        지하철구간_생성_응답상태_201_검증(response);
    }
}
