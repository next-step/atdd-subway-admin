package nextstep.subway.line.domain;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("구간목록 관련 기능")
class SectionsTest {

    private Station 삼성역;
    private Station 잠실역;
    private Line _2호선;
    private Section section;
    private Sections sections;

    @BeforeEach
    void beforEach() {
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
        _2호선 = new Line("_2호선", "bg-green-200", 삼성역, 잠실역, 1000L);
        section = new Section(_2호선, 삼성역, 잠실역, 1000L);
        sections = new Sections(section);
    }

    @DisplayName("`Sections` 생성")
    @Test
    void createSections() {
        // Then
        assertAll(
                () -> assertNotNull(sections),
                () -> assertThat(sections.getStations())
                        .extracting(Station::getName)
                        .containsExactly("삼성역", "잠실역")
        );
    }

    @DisplayName("`Sections`에 새로운 구간 `Section` 추가")
    @Test
    void addSection() {
        // Given
        Section addSection = new Section(_2호선, new Station("종합운동장역"), 잠실역, 500L);
        // When
        sections.add(addSection);
        // Then
        assertThat(sections.getStations())
                .extracting(Station::getName)
                .containsExactly("삼성역", "종합운동장역", "잠실역");
    }

    @DisplayName("`Sections`에 새로운 구간 `Section` 추가시 예외 - 이미 등록된 구간 입니다.")
    @Test
    void exceptionToAddSection1() {
        // Given
        Section addSection = new Section(_2호선, 삼성역, 잠실역, 500L);
        // When & Then
        assertThatThrownBy(
                () -> sections.add(addSection)
        ).isInstanceOf(CustomException.class)
                .hasMessage("이미 등록된 구간 입니다.");
    }

    @DisplayName("`Sections`에 새로운 구간 `Section` 추가시 예외 - 등록할 수 없는 구간 입니다.")
    @Test
    void exceptionToAddSection2() {
        // Given
        Section addSection = new Section(_2호선, new Station("종합운동장역"), new Station("잠실새내역"), 500L);
        // When & Then
        assertThatThrownBy(
                () -> sections.add(addSection)
        ).isInstanceOf(CustomException.class)
                .hasMessage("등록할 수 없는 구간 입니다.");
    }

    @DisplayName("`Sections`에 속한 모든 `Station` 반환")
    @Test
    void getStations() {
        // Given
        Section addSection = new Section(_2호선, 삼성역, new Station("종합운동장역"), 500L);
        sections.add(addSection);
        // When
        List<Station> stations = sections.getStations();
        // Then
        assertThat(stations)
                .extracting(Station::getName)
                .containsExactly("삼성역", "종합운동장역", "잠실역");
    }
}
