package nextstep.subway.line.domain;

import static nextstep.subway.station.StationAcceptanceTest.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.DuplicateSectionStationException;
import nextstep.subway.station.domain.Station;

public class SectionTest {

    @Test
    void 동일한_상행_하행_입력시_예외() {
        //given
        Station stationGangnam = new Station(1L, 강남역);

        // then
        Assertions.assertThatThrownBy(() -> {
                      Section section = new Section(stationGangnam, stationGangnam, SectionType.MIDDLE, new Distance(10));
                  }).isInstanceOf(DuplicateSectionStationException.class)
                  .hasMessage("상행선과 하행선은 동일할 수 없습니다.");
    }

    @Test
    void 상행추가_기존_상행과_새로운_하행역이_같다() {
        // given
        Station stationGangnam = new Station(1L, "강남역");
        Station stationSinChon = new Station(2L, "신촌역");
        Section section = new Section(stationGangnam, stationSinChon, SectionType.FIRST, new Distance(10));

        // when
        Station stationYoungSan = new Station(3L, "용산역");
        Section newSection = new Section(stationYoungSan, stationGangnam, SectionType.MIDDLE, new Distance(5));
        boolean isStationTrue = section.findUpStation(newSection);

        // then
        Assertions.assertThat(isStationTrue).isTrue();
    }

    @Test
    void 하행추가_기존_하행과_새로운_상행역이_같다() {
        // given
        Station stationGangnam = new Station(1L, "강남역");
        Station stationSinChon = new Station(2L, "신촌역");
        Section section = new Section(stationGangnam, stationSinChon, SectionType.LAST, new Distance(10));

        // when
        Station stationYoungSan = new Station(3L, "용산역");
        Section newSection = new Section(stationSinChon, stationYoungSan, SectionType.MIDDLE, new Distance(5));
        boolean isStationTrue = section.findDownStation(newSection);

        // then
        Assertions.assertThat(isStationTrue).isTrue();
    }
}
