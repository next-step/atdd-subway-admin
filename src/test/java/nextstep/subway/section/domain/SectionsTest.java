package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SectionsTest {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    private static Station upStation = new Station("신림역");
    private static Station downStation = new Station("서울대입구역");
    private static Station newUpStation = new Station("봉천역");
    private static Station newDownStation = new Station("낙성대역");
    private static Section section;
    private static Sections sections;

    @BeforeEach
    public void init() {
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newDownStation, newUpStation));
        section = new Section(upStation, downStation, new Distance(10L));
        sections = new Sections();
        sections.add(section);
    }

    @Test
    @DisplayName("정상적으로 구간이 등록되었을 경우 구간의 개수가 증가한다.")
    void createSection_add() {
        Section newSection = new Section(upStation, newUpStation, new Distance(5L));

        sections.add(newSection);

        assertThat(sections.size()).isEqualTo(2);
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Sections 에 노선을 추가할 때 예외처리에 대한 케이스 확인")
    @MethodSource("providerCreateSection_add_failCase")
    void createSection_add_fail(String name, Station upStation, Station downStation) {
        Section newSection = new Section(upStation, downStation, new Distance(5L));

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(newSection));
    }

    @Test
    @DisplayName("노선에 등록된 지하철역을 상하행 순서로 출력한다.")
    void orderStationsOfLine() {
        Section newSection = new Section(upStation, newUpStation, new Distance(5L));

        sections.add(newSection);

        assertThat(sections.orderStationsOfLine()).containsExactly(upStation, newUpStation, downStation);
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("노선에서 특정 지하철역을 삭제할 때, 발생할 수 있는 예외처리 확인")
    @MethodSource("providerRemoveStationInSection_failCase")
    void removeStationInSection_fail(String name, Station removeStation) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.removeStationInSection(removeStation));
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("노선에서 특정 지하철역을 삭제되었을 떄, 남은 노선의 구간 개수를 확인한다.")
    @MethodSource("providerRemoveStationInSection_successCase")
    void removeStationInSection_success(String name, Station removeStation, List<Station> resultStations) {
        Section newSection = new Section(upStation, newDownStation, new Distance(5L));
        sections.add(newSection);

        sections.removeStationInSection(removeStation);

        assertThat(sections.orderStationsOfLine()).containsExactly(resultStations.toArray(new Station[0]));
    }

    static Stream<Arguments> providerRemoveStationInSection_successCase() {
        return Stream.of(
            Arguments.of(
                "상행종점역이 삭제되었을 때", upStation, Arrays.asList(newDownStation, downStation)
            ),
            Arguments.of(
                "하행종점역이 삭제되었을 때", downStation, Arrays.asList(upStation, newDownStation)
            ),
            Arguments.of(
                "상하행종점역 사이의 역이 삭제되었을 때", newDownStation, Arrays.asList(upStation, downStation)
            )
        );
    }

    static Stream<Arguments> providerRemoveStationInSection_failCase() {
        return Stream.of(
            Arguments.of(
                "노선에 남은 구간이 하나일 때", upStation
            ),
            Arguments.of(
                "노선에 해당 지하철역이 없을 때", newDownStation
            )
        );
    }

    static Stream<Arguments> providerCreateSection_add_failCase(){
        return Stream.of(
            Arguments.of(
                "종점역이 노선에 등록되어있지 않는 경우 에러출력", newUpStation, newDownStation
            ),
            Arguments.of(
                "이미 상하행종점역이 모두 노선에 존재하는 경우 에러 출력", downStation, upStation
            )
        );
    }

}
