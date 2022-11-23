package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    Station station1 = new Station(1L, "판교역");
    Station station2 = new Station(2L, "강남역");
    Station station3 = new Station(3L, "중앙역");
    Section section = new Section(new Line("신분당선", "red"), 10, station1, station2);
    Section section2 = new Section(new Line("신분당선", "red"), 5, station3, station1);


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 12})
    void validateLength(int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> section.validateLength(distance));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void validateAlreadyExistsStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> section.validateAlreadyExistsStation(station1, station2));
    }

    @DisplayName("등록한 상행역이 같은지 비교한다")
    @Test
    void isEqualsUpStation() {
        assertThat(section.isEqualsUpStation(station1)).isTrue();
    }

    @DisplayName("등록한 하행역이 같은지 비교한다")
    @Test
    void isEqualsDownStation() {
        assertThat(section.isEqualsDownStation(station2)).isTrue();
    }

    @DisplayName("상행역을 기준으로 역사이에 새로운 역을 등록한다")
    @Test
    void createBetweenSectionByUpStation() {
        section.createBetweenSectionByUpStation(5, station1, station3);

        assertThat(section.getUpStation()).isEqualTo(station3);
    }

    @DisplayName("하행역을 기준으로 역사이에 새로운 역을 등록한다")
    @Test
    void createBetweenSectionByDownStation() {
        section.createBetweenSectionByDownStation(5, station3, station2);

        assertThat(section.getDownStation()).isEqualTo(station3);
    }

    @DisplayName("기존구간 앞에 구간추가")
    @Test
    void createPrependSection() {
        Section prependSection = section.createPrependSection(5, station3, station1);

        assertThat(section.getUpStation()).isEqualTo(station1);
        assertThat(section.getDownStation()).isEqualTo(station2);
        assertThat(prependSection.getUpStation()).isEqualTo(station3);
        assertThat(prependSection.getDownStation()).isEqualTo(station1);
    }

    @DisplayName("기존구간 뒤에 구간추가")
    @Test
    void createAppendSection() {
        Section appendSection = section.createAppendSection(5, station2, station3);

        assertThat(section.getUpStation()).isEqualTo(station1);
        assertThat(section.getDownStation()).isEqualTo(station2);
        assertThat(appendSection.getUpStation()).isEqualTo(station2);
        assertThat(appendSection.getDownStation()).isEqualTo(station3);
    }

    @DisplayName("현 구간의 앞에 추가로 구간이 존재하면 해당구간 뒤에 현 구간을 추가한다")
    @Test
    void ifExistPreSectionThenSetNextSection() {
        Section newSection = new Section(new Line("신분당선", "red"), 10, section, section2, station1, station2);

        newSection.ifExistPreSectionThenSetNextSection(section);

        assertThat(newSection.isFirstSection()).isFalse();
    }

    @DisplayName("현 구간의 뒤에 추가로 구간이 존재하면 해당구간 앞에 현 구간을 추가한다")
    @Test
    void ifExistNextSectionThenSetPreSection() {
        Section newSection = new Section(new Line("신분당선", "red"), 10, section, section2, station1, station2);

        newSection.ifExistNextSectionThenSetPreSection(section2);

        assertThat(newSection.isLastSection()).isFalse();
    }

    @DisplayName("만약 다음 구간이 존재하면 역이름 목록을 추가 한다")
    @Test
    void ifExistNextSectionThenAddStationNames() {
        Section newSection = new Section(new Line("신분당선", "red"), 10, section, section2, station1, station2);
        Set<String> stationNames = new LinkedHashSet<>();

        newSection.ifExistNextSectionThenAddStationNames(stationNames);

        assertThat(stationNames).hasSize(2);
        assertThat(stationNames).containsExactly("중앙역", "판교역");
    }

    @DisplayName("만약 다음 구간이 존재하면 길이 목록을 추가한다")
    @Test
    void ifExistNextSectionThenAddDistances() {
        Section newSection = new Section(new Line("신분당선", "red"), 10, section, section2, station1, station2);
        List<Integer> distances = new ArrayList<>();

        newSection.ifExistNextSectionThenAddDistances(distances);

        assertThat(distances).hasSize(1);
        assertThat(distances).containsExactly(5);
    }

    @DisplayName("이전구간과 다음구간과의 연관관계를 끊는다")
    @Test
    void removeSection() {
        Section newSection = new Section(new Line("신분당선", "red"), 10, section, section2, station1, station2);
        newSection.ifExistPreSectionThenSetNextSection(newSection);
        newSection.ifExistNextSectionThenSetPreSection(newSection);

        newSection.removeSection();

        assertThat(section.isLastSection()).isTrue();
        assertThat(section2.isFirstSection()).isTrue();
    }
}
