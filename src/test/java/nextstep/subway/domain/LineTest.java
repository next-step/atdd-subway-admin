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
        line.addDefaultSection(10, upStation, downStation);
    }

    @DisplayName("기존 노선에 구간 추가시 상행 하행역이 모두 동일하거나 둘다 다를 경우 EX 발생")
    @Test
    void addSection() {
        assertThatIllegalArgumentException().isThrownBy(() -> line.addSection(4, upStation, downStation));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(4, new Station(3L, "판교역"), new Station(4L, "중앙역")));
    }

    @Test
    void 상행역으로_새로운_구간_생성시_길이가_같으면_EX_발생() {
        assertThatIllegalArgumentException().isThrownBy(() -> line.addSection(10, upStation, new Station(3L, "판교역")));
    }

    @Test
    void 상행역이_같으면_새로운_구간_생성() {
        line.addSection(4, upStation, new Station(3L, "판교역"));

        assertThat(line.getSectionList()).hasSize(2);
    }

    @Test
    void 하행역으로_새로운_구간_생성시_길이가_같으면_EX_발생() {
        assertThatIllegalArgumentException().isThrownBy(() -> line.addSection(10, new Station(3L, "판교역"), downStation));
    }

    @Test
    void 하행역이_같으면_새로운_구간_생성() {
        line.addSection(4, new Station(3L, "판교역"), downStation);

        assertThat(line.getSectionList()).hasSize(2);
    }

    @Test
    void 구간이_하나인_노선은_제거_불가능() {
        assertThatIllegalArgumentException().isThrownBy(() -> line.deleteSectionByStation(upStation));
    }
}
