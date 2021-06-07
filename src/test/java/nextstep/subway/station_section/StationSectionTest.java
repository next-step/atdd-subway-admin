package nextstep.subway.station_section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationSectionTest {
    @DisplayName("역구간 생성시 역구간 확인")
    @Test
    public void 역구간생성시_역구간확인() {
        //given
        Line line = Line.create("5호선", "purple");
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");
        Section section = Section.create(line, upStation, downStation, 10);

        //when
        StationSection stationSection = StationSection.create(upStation, section, StationType.UP);

        //then
        assertThat(stationSection.stationName()).isEqualTo("미사역");
        assertThat(stationSection.isUpStationType()).isTrue();
        assertThat(stationSection.isDownStationType()).isFalse();
    }

    @DisplayName("역구간 생성시 유효성 검사")
    @Test
    public void 역구간생성시_예외발생확인() {
        //given
        Line line = Line.create("5호선", "purple");
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");
        Section section = Section.create(line, upStation, downStation, 10);

        //when
        //then
        assertThatThrownBy(() -> StationSection.create(null, section, StationType.UP))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> StationSection.create(upStation, null, StationType.UP))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> StationSection.create(upStation, section, null))
                .isInstanceOf(NullPointerException.class);
    }
}
