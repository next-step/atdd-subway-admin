package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    Station station1 = new Station(1L, "판교역");
    Station station2 = new Station(2L, "강남역");
    Station station3 = new Station(3L, "중앙역");
    Station station4 = new Station(4L, "경기 광주역");
    Section section = new Section(new Line("신분당선", "red"), 4, station1, station2);


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6})
    void validateLength(int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> section.validateLength(distance));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void validateAlreadyExistsStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> section.validateAlreadyExistsStation(station1, station2));
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void validateNotExistsStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> section.validateNotExistsStation(station3, station4));
    }

    @DisplayName("새로운 구간을 생성한다")
    @Test
    void createNewSection() {
        Section newSection = section.createNewSection(10, 1000, station1, station2);

        assertThat(newSection.isEqualsUpStation(station1)).isTrue();
        assertThat(newSection.isEqualsDownStation(station2)).isTrue();
    }

    @DisplayName("기존 구간의 하행역에 또다른 하행역을 이어붙인다")
    @Test
    void createNewAppendDownStation() {
        Section newSection = section.createNewAppendDownStation(10, 1000, station3);

        assertThat(newSection.isEqualsUpStation(station2)).isTrue();
        assertThat(newSection.isEqualsDownStation(station3)).isTrue();
    }

    @DisplayName("기존 구간의 상행역을 또다른 상행역을 이어붙인다")
    @Test
    void createNewPrependUpStation() {
        Section newSection = section.createNewPrependUpStation(10, 1000, station3);

        assertThat(newSection.isEqualsUpStation(station3)).isTrue();
        assertThat(newSection.isEqualsDownStation(station1)).isTrue();
    }
}
