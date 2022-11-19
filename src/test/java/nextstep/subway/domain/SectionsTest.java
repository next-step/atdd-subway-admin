package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    Sections sections = new Sections();
    Station upStation = new Station(1L, "경기 광주역");
    Station downStation = new Station(2L, "모란역");


    @BeforeEach
    void beforeEach() {
        sections.addSection(new Section(null, 10, upStation, downStation));
    }

    @DisplayName("상행역이 같은면 반환한다")
    @Test
    void findSameUpStation() {
        Section findSection = sections.findSameUpStation(upStation).get();

        assertThat(findSection.getUpStation()).isEqualTo(upStation);
    }

    @Test
    void findSameDownStation() {
        Section findSection = sections.findSameDownStation(downStation).get();

        assertThat(findSection.getDownStation()).isEqualTo(downStation);
    }

    @DisplayName("상행역과 하행역에 이미 같은 역이 존재하면 EX 발생")
    @Test
    void validateAlreadyExistsStation() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.validateAlreadyExistsStation(upStation, downStation));
    }

    @DisplayName("상행역과 하행역 모두 같은 역이 존재하지 않으면 EX 발생")
    @Test
    void validateNotExistsStation() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.validateNotExistsStation(new Station(3L, "판교역"), new Station(4L, "중앙역")));
    }

}
