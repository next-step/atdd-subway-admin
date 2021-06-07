package nextstep.subway.section;

import nextstep.subway.exception.ValueOutOfBoundsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    @DisplayName("구간 생성시 구간 확인")
    @Test
    public void 구간생성시_구간확인() {
        //given
        Line line = Line.create("5호선", "purple");
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");

        //when
        Section section = Section.create(line, upStation, downStation, 10);

        //then
        assertThat(section.upStationName()).isEqualTo("미사역");
        assertThat(section.downStationName()).isEqualTo("하남풍산역");
        assertThat(section.distance()).isEqualTo(10);
    }

    @DisplayName("구간 생성시 유효성 검사")
    @Test
    public void 구간생성시_예외발생확인() {
        //given
        Line line = Line.create("5호선", "purple");
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");

        //when
        //then
        assertThatThrownBy(() -> Section.create(null, upStation, downStation, 10))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Section.create(line, null, downStation, 10))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Section.create(line, upStation, null, 10))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Section.create(line, upStation, downStation, 0))
                .isInstanceOf(ValueOutOfBoundsException.class);
    }

    @DisplayName("구간 생성시 역구간 생성 확인")
    @Test
    public void 구간생성시_역구간생성확인() {
        //given
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");
        Line line = Line.create("5호선", "purple");

        //when
        Section section = Section.create(line, upStation, downStation, 10);

        //then
        assertThat(section.stationSections().size()).isEqualTo(2);
    }
}