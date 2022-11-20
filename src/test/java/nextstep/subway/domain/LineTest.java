package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    Line line = new Line();
    Station upStation = new Station(1L, "경기 광주역");
    Station downStation = new Station(2L, "모란역");

    @BeforeEach
    void beforeEach() {
        line.addSection(10, upStation, downStation);
    }

    @DisplayName("추가한 구간 수 만큼 가지고 있는지 확인")
    @Test
    void addSection() {
        assertThat(line.getSectionList()).hasSize(1);
    }

    @DisplayName("상행 하행역이 모두 동일하거나 둘다 다를 경우 EX 발생")
    @Test
    void validateAlreadyAndNotExistsStations() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.validateAlreadyAndNotExistsStations(upStation, downStation));
        assertThatIllegalArgumentException().isThrownBy(() ->
                line.validateAlreadyAndNotExistsStations(new Station(3L, "판교역"), new Station(4L, "중앙역"))
        );
    }

    @DisplayName("상행역이 같으면 새로운 구간 생성, 단 길이가 같으면 EX 발생")
    @Test
    void isFindSameUpStationThenCreateNewSection() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> line.isFindSameUpStationThenCreateNewSection(upStation, new Station(3L, "판교역"), 10)
        );

        line.isFindSameUpStationThenCreateNewSection(upStation, new Station(3L, "판교역"), 4);

        assertThat(line.getSectionList()).hasSize(2);
    }

    @DisplayName("하행역이 같으면 새로운 구간 생성, 단 길이가 같으면 EX 발생")
    @Test
    void isFindSameDownStationThenCreateNewSection() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> line.isFindSameDownStationThenCreateNewSection(new Station(3L, "판교역"), downStation, 10)
        );

        line.isFindSameDownStationThenCreateNewSection(new Station(3L, "판교역"), downStation, 4);

        assertThat(line.getSectionList()).hasSize(2);
    }

}
