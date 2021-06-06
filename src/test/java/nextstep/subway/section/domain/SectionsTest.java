package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

/**
 * Sections 클래스의 기능 검증 테스트
 */
public class SectionsTest {
    @Test
    @DisplayName("상행역에서 하행역으로 정렬된 역 목록 반환")
    void getStationResponses() {
        // given
        Station station2 = new Station("역삼역");
        Station station3 = new Station("교대역");
        Station station1 = new Station("강남역");
        Station station4 = new Station("서초역");
        Station station5 = new Station("방배역");
        List<Station> lineStations = Arrays.asList(station2, station3, station1, station4, station5);
        Section section1 = new Section(station2, station3, 3);
        Section section2 = new Section(station1, station4, 10);
        Section section3 = new Section(station4, station5, 3);
        Section section4 = new Section(station3, station1, 3);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3, section4));

        // when
        List<Station> createStations = sections.getSortedStations();

        // then
        List<StationResponse> targetStationResponses = lineStations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        List<StationResponse> resultStationResponses = createStations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        assertThat(Arrays.equals(resultStationResponses.toArray(), targetStationResponses.toArray())).isTrue();
    }

    @Test
    @DisplayName("일급 컬렉션 초기화 유효성 검증")
    void sections_is_empty_exception() {
        // then
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
    }
}
