package nextstep.subway.line.domain;

import nextstep.subway.line.exception.NotFoundUpAndDownStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Sections sections;
    private Station 강남역;
    private Station 잠실역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        잠실역 = new Station("잠실역");
        Section section = new Section(강남역, 잠실역, new Distance(10));
        sections = new Sections(section);
    }

    @DisplayName("상행역이 동일한 내부 구간을 등록한다.")
    @Test
    void addSectionEqualUpStation() {
        Station 삼성역 = new Station("삼성역");
        Section section = new Section(강남역, 삼성역, new Distance(4));

        sections.addSection(section);

        List<String> savedNames = sections.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(savedNames).containsExactly("강남역", "삼성역", "잠실역");
    }

    @DisplayName("하행역이 동일한 내부 구간을 등록한다.")
    @Test
    void addSectionEqualDownStation() {
        Station 삼성역 = new Station("삼성역");
        Section section = new Section(삼성역, 잠실역, new Distance(4));

        sections.addSection(section);

        List<String> savedNames = sections.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(savedNames).containsExactly("강남역", "삼성역", "잠실역");
    }

    @DisplayName("새로운 상행 구간을 등록한다.")
    @Test
    void addUpSection() {
        Station 사당역 = new Station("사당역");
        Section section = new Section(사당역, 강남역, new Distance(4));

        sections.addSection(section);

        List<String> savedNames = sections.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(savedNames).containsExactly("사당역", "강남역", "잠실역");
    }

    @DisplayName("새로운 하행 구간을 등록한다.")
    @Test
    void addDownSection() {
        Station 강변역 = new Station("강변역");
        Section section = new Section(잠실역, 강변역, new Distance(4));

        sections.addSection(section);

        List<String> savedNames = sections.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(savedNames).containsExactly("강남역", "잠실역", "강변역");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 노선에 등록되어있지 않은 구간을 등록한다.")
    @Test
    void notExistsStationInLine() {
        Station 삼성역 = new Station("삼성역");
        Station 홍대입구역 = new Station("홍대입구역");
        Section section = new Section(삼성역, 홍대입구역, new Distance(20));

        ThrowableAssert.ThrowingCallable throwingCallable =
                () -> sections.addSection(section);

        assertThatThrownBy(throwingCallable)
                .isInstanceOf(NotFoundUpAndDownStation.class);
    }

    @DisplayName("구간에 속한 지하철역 조회(상행 -> 하행 순서)")
    @Test
    void getStations() {
        강남역 = new Station("강남역");
        잠실역 = new Station("잠실역");
        Section section = new Section(강남역, 잠실역, new Distance(10));
    }

}
