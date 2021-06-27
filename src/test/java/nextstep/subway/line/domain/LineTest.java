package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    @DisplayName("노선에 하행과 상행구간이 저장된다.")
    void addSections_test() {
        //given
        Line line = new Line("신붇당선", "red");
        Station 판교역 = new Station("판교역");
        Station 이매역 = new Station("이매역");
        Section 구간 = new Section(판교역, 이매역, 10);

        //when
        Line addedSectionLine = line.addSection(구간);

        //then
        assertThat(addedSectionLine.getSections()).containsExactly(
                구간
        );
    }

    @Test
    @DisplayName("노선에 존재하는 역들을 추출한다.")
    void extractStationsResponse_test() {
        //given
        Line line = new Line("신붇당선", "red");
        Station 판교역 = new Station("판교역");
        Station 이매역 = new Station("이매역");
        Section 구간 = new Section(판교역, 이매역, 10);
        Line addedSectionLine = line.addSection(구간);

        //when
        StationsResponse stationsResponse = addedSectionLine.extractStationsResponse();

        //then
        assertThat(stationsResponse.getStations()).hasSize(2);
    }
}
