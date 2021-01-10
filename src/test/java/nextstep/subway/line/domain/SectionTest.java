package nextstep.subway.line.domain;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("구간 관련 기능")
class SectionTest {

    private Station 삼성역;
    private Station 잠실역;
    private Line _2호선;

    @BeforeEach
    void beforEach() {
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
        _2호선 = new Line("_2호선", "bg-green-200", 삼성역, 잠실역, 1000L);
    }

    @DisplayName("`Section` 생성")
    @Test
    void createSection() {
        // When
        Section section = new Section(_2호선, 삼성역, 잠실역, 1000L);
        // Then
        assertNotNull(section);
    }

    @DisplayName("`Section` 생성 예외 확인 - 구간의 노선 정보가 포함되어야합니다.")
    @Test
    void exceptionToCreateSectionWithoutLine() {
        // Given
        Station 종합운동장역 = new Station("종합운동장역");
        // When & Then
        assertThatThrownBy(
                () -> new Section(null, 종합운동장역, 잠실역, 1000L)
        ).isInstanceOf(CustomException.class)
                .hasMessage("구간의 노선 정보가 포함되어야합니다.");
    }

    @DisplayName("`Section` 생성 예외 확인 - 구간의 상행 또는 하행역 정보는 추가되어야합니다.")
    @Test
    void exceptionToCreateSectionWithoutStations() {
        // When & Then
        assertThatThrownBy(
                () -> new Section(_2호선, null, null, 1000L)
        ).isInstanceOf(CustomException.class)
                .hasMessage("구간의 상행 또는 하행역 정보는 추가되어야합니다.");
    }

    @DisplayName("`Section`안에 새로 추가된 경우, `Section`의 하행역 변경")
    @Test
    void updateDownStation() {
        // Given
        Station 종합운동장역 = new Station("종합운동장역");
        Section addedSection = new Section(_2호선, 종합운동장역, 잠실역, 500L);
        Section actual = new Section(_2호선, 삼성역, 잠실역, 1000L);
        // When
        actual.updateDownStation(addedSection);
        // Then
        assertAll(
                () -> assertThat(actual.getLine()).isEqualTo(_2호선),
                () -> assertThat(actual.getUpStation()).isEqualTo(삼성역),
                () -> assertThat(actual.getDownStation()).isEqualTo(종합운동장역)
        );
    }

    @DisplayName("`Section`안에 새로 추가된 경우, `Section`의 하행역 변경시 예외 확인 - 역과 역 사이의 거리보다 좁은 거리를 입력해주세요")
    @Test
    void exceptionToUpdateDownStation() {
        // Given
        Station 종합운동장역 = new Station("종합운동장역");
        Section addedSection = new Section(_2호선, 종합운동장역, 잠실역, 1000L);
        Section actual = new Section(_2호선, 삼성역, 잠실역, 1000L);
        // When & Then
        assertThatThrownBy(
                () -> actual.updateDownStation(addedSection)
        ).isInstanceOf(CustomException.class)
                .hasMessage("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
    }

    @DisplayName("`Section`안에 새로 추가된 경우, `Section`의 상행역 변경")
    @Test
    void updateUpStation() {
        // Given
        Station 종합운동장역 = new Station("종합운동장역");
        Section addedSection = new Section(_2호선, 삼성역, 종합운동장역, 500L);
        Section actual = new Section(_2호선, 삼성역, 잠실역, 1000L);
        // When
        actual.updateUpStation(addedSection);
        // Then
        assertAll(
                () -> assertThat(actual.getLine()).isEqualTo(_2호선),
                () -> assertThat(actual.getUpStation()).isEqualTo(종합운동장역),
                () -> assertThat(actual.getDownStation()).isEqualTo(잠실역)
        );
    }

    @DisplayName("`Section`안에 새로 추가된 경우, `Section`의 상행역 변경시 예외 확인 - 역과 역 사이의 거리보다 좁은 거리를 입력해주세요")
    @Test
    void exceptionToUpdateUpStation() {
        // Given
        Station 종합운동장역 = new Station("종합운동장역");
        Section addedSection1 = new Section(_2호선, 삼성역, 종합운동장역, 1000L);
        Section actual = new Section(_2호선, 삼성역, 잠실역, 1000L);
        // When & Then
        assertThatThrownBy(
                () -> actual.updateUpStation(addedSection1)
        ).isInstanceOf(CustomException.class)
                .hasMessage("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
    }
}
