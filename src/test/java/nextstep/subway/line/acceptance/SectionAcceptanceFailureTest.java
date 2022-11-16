package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.acceptance.StationAcceptance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 구간 관련 실패 테스트")
class SectionAcceptanceFailureTest extends AcceptanceTest {

    /**
     * Given 지하철 노선을 생성하고
     * When 이미 등록되어 있는 구간을 생성하면
     * Then 지하철 구간을 생성할 수 없다
     */
    @DisplayName("이미 등록되어 있는 구간을 생성한다.")
    @Test
    void updateAlreadyAddedSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station2, 10));

        // when
        ExtractableResponse<Response> response =
                SectionAcceptance.update_section(lineId, station1, station2, 10);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 거리가 추가하려는 구간의 거리보다 크거나 같으면
     * Then 지하철 구간을 생성할 수 없다
     */
    @DisplayName("잘못된 거리 정보로 지하철 구간을 생성한다.")
    @Test
    void updateWithInvalidDistance() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station3, 10));

        // when
        ExtractableResponse<Response> response =
                SectionAcceptance.update_section(lineId, station1, station2, 20);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역과 하행역이 모두 기존 구간에 포함되어 있지 않으면
     * Then 지하철 구간을 생성할 수 없다
     */
    @DisplayName("상행역과 하행역이 모두 기존 구간에 포함되어 있지 않을 때 지하철 구간을 생성한다.")
    @Test
    void updateWhenPositionNotFound() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("잠실역"));
        Long station4 = StationAcceptance.getStationId(StationAcceptance.create_station("천호역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station2, 10));

        // when
        ExtractableResponse<Response> response =
                SectionAcceptance.update_section(lineId, station3, station4, 20);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }
}
