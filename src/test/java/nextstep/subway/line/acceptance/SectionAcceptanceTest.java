package nextstep.subway.line.acceptance;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.acceptance.StationAcceptance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    /**
     * Given 지하철 노선을 생성하고
     * When 역 사이에 새로운 구간을 생성하면
     * Then 지하철 구간이 분리된다
     */
    @DisplayName("역 사이에 새로운 구간을 생성한다.")
    @Test
    void updateBetweenSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station3, 10));

        // when
        SectionAcceptance.update_section(lineId, station1, station2, 7);

        // then
        List<Map<String, Object>> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertThat(sections).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 맨 앞에 새로운 구간을 생성하면
     * Then 지하철 구간이 생성된다
     */
    @DisplayName("맨 앞에 새로운 구간을 생성한다.")
    @Test
    void updateFirstSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station2, station3, 10));

        // when
        SectionAcceptance.update_section(lineId, station1, station2, 7);

        // then
        List<Map<String, Object>> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertThat(sections).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 맨 뒤에 새로운 구간을 생성하면
     * Then 지하철 구간이 생성된다
     */
    @DisplayName("맨 뒤에 새로운 구간을 생성한다.")
    @Test
    void updateLastSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station2, 10));

        // when
        SectionAcceptance.update_section(lineId, station2, station3, 7);

        // then
        List<Map<String, Object>> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertThat(sections).hasSize(2);
    }
}
