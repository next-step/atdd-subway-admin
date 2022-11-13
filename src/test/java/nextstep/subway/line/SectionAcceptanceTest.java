package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceStep.등록된_지하철노선;
import static nextstep.subway.line.LineAcceptanceStep.지하철노선_목록_조회_응답상태_200_검증;
import static nextstep.subway.line.SectionAcceptanceStep.*;
import static nextstep.subway.station.StationAcceptanceStep.등록된_지하철역;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long lineId;
    private Long stationId1;
    private Long stationId2;
    private Long registeredStationId1;
    private Long registeredStationId2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> createLineResponse = 등록된_지하철노선("2호선", "indigo darken-4", 10, "신촌역", "을지로역");
        LineResponse lineResponse = createLineResponse.as(LineResponse.class);
        lineId = lineResponse.getId();

        List<StationResponse> stations = lineResponse.getStations();
        registeredStationId1 = stations.get(0).getId();
        registeredStationId2 = stations.get(1).getId();


        ExtractableResponse<Response> createdStationResponse1 = 등록된_지하철역("용산역");
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        ExtractableResponse<Response> createdStationResponse2 = 등록된_지하철역("노량진역");
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 응답상태 201을 반환한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, registeredStationId1, stationId1, 3);

        // then
        지하철구간_생성_응답상태_201_검증(response);
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 응답상태 201을 반환한다.")
    @Test
    void createSectionToAscentEndpoint() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, stationId1, registeredStationId1, 3);

        // then
        지하철구간_생성_응답상태_201_검증(response);
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 응답상태 201을 반환한다.")
    @Test
    void createSectionToDeAscentEndpoint() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, registeredStationId2, stationId1, 3);

        // then
        지하철구간_생성_응답상태_201_검증(response);
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 응답상태 400을 반환한다.")
    @Test
    void createSection_distanceExcess() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, registeredStationId1, stationId1, 11);

        // then
        지하철구간_생성_응답상태_400_검증(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 응답상태 400을 반환한다.")
    @Test
    void createSection_duplicateStation() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, registeredStationId1, registeredStationId2, 3);

        // then
        지하철구간_생성_응답상태_400_검증(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 응답상태 400을 반환한다.")
    @Test
    void createSection_noStation() {
        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_등록_요청(lineId, stationId1, stationId2, 3);

        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));
        // then
        지하철구간_생성_응답상태_400_검증(response);
    }

    @DisplayName("종점을 제거하는 경우")
    @Test
    void deleteSection_endpoint() {
        // given
        지하철노선에_구간_등록_요청(lineId, registeredStationId2, stationId1, 3);
        지하철노선에_구간_등록_요청(lineId, stationId1, stationId2, 3);
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));

        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_삭제_요청(lineId, stationId2);

        // then
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));
        지하철구간_삭제_응답상태_204_검증(response);
    }

    @DisplayName("가운데 역을 제거하는 경우")
    @Test
    void deleteSection_midpoint() {
        // given
        지하철노선에_구간_등록_요청(lineId, registeredStationId2, stationId1, 3);
        지하철노선에_구간_등록_요청(lineId, stationId1, stationId2, 3);
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));

        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_삭제_요청(lineId, stationId1);

        // then
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));
        지하철구간_삭제_응답상태_204_검증(response);
    }

    @DisplayName("구간이 하나인 노선에서 역을 제거하는 경우")
    @Test
    void deleteSection_lastSection() {
        // given
        지하철노선_목록_조회_응답상태_200_검증(지하철노선_구간_목록_조회_요청(lineId));

        // when
        ExtractableResponse<Response> response = 지하철노선에_구간_삭제_요청(lineId, stationId1);

        // then
        지하철구간_삭제_응답상태_400_검증(response);
    }
}
