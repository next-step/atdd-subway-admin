package nextstep.subway.line.acceptance;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.acceptance.StationAcceptance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    /**
     * Given 지하철 노선을 생성하고
     * When 역 사이에 새로운 구간을 생성하면
     * Then 지하철 구간이 분리된다
     */
    @DisplayName("역 사이에 새로운 구간을 생성한다.")
    @Test
    void updateMiddleSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station3, 10));

        // when
        SectionAcceptance.update_section(lineId, station1, station2, 7);

        // then
        List<SectionResponse> sections = SectionAcceptance.section_list_was_queried(lineId);
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
        List<SectionResponse> sections = SectionAcceptance.section_list_was_queried(lineId);
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
        List<SectionResponse> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertThat(sections).hasSize(2);
    }

    /**
     * Given 지하철 노선과 지하철 구간을 생성하고
     * When 역 사이의 구간을 삭제하면
     * Then 기존 구간이 연장된다
     */
    @DisplayName("역 사이 구간을 삭제한다.")
    @Test
    void deleteMiddleSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station2, 10));
        SectionAcceptance.update_section(lineId, station2, station3, 10);

        // when
        SectionAcceptance.delete_section(lineId, station2);

        // then
        List<SectionResponse> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0)).satisfies(section -> {
                    assertEquals(station1, section.getUpStation().getId());
                    assertEquals(station3, section.getDownStation().getId());
                    assertEquals(20, section.getDistance());
                })
        );
    }

    /**
     * Given 지하철 노선과 지하철 구간을 생성하고
     * When 맨 앞의 구간을 삭제하면
     * Then 구간이 삭제된다
     */
    @DisplayName("맨 앞의 구간을 삭제한다.")
    @Test
    void deleteFirstSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station2, 10));
        SectionAcceptance.update_section(lineId, station2, station3, 10);

        // when
        SectionAcceptance.delete_section(lineId, station1);

        // then
        List<SectionResponse> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0)).satisfies(section -> {
                    assertEquals(station2, section.getUpStation().getId());
                    assertEquals(station3, section.getDownStation().getId());
                    assertEquals(10, section.getDistance());
                })
        );
    }

    /**
     * Given 지하철 노선과 지하철 구간을 생성하고
     * When 맨 뒤의 구간을 삭제하면
     * Then 구간이 삭제된다
     */
    @DisplayName("맨 뒤의 구간을 삭제한다.")
    @Test
    void deleteLastSection() {
        // given
        Long station1 = StationAcceptance.getStationId(StationAcceptance.create_station("교대역"));
        Long station2 = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long station3 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("2호선", "bg-green-600", station1, station2, 10));
        SectionAcceptance.update_section(lineId, station2, station3, 10);

        // when
        SectionAcceptance.delete_section(lineId, station3);

        // then
        List<SectionResponse> sections = SectionAcceptance.section_list_was_queried(lineId);
        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0)).satisfies(section -> {
                    assertEquals(station1, section.getUpStation().getId());
                    assertEquals(station2, section.getDownStation().getId());
                    assertEquals(10, section.getDistance());
                })
        );
    }
}
