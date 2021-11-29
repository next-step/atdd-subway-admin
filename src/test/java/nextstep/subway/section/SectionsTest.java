package nextstep.subway.section;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    @Test
    void getStationsTest() {
        // given
        List<Station> stations = new ArrayList<>();
        Sections stations1 = new Sections();
        Sections stations2 = new Sections();
        Station 삼성역 = new Station("삼성역");
        Station 신사역 = new Station("신사역");
        Station 사당역 = new Station("사당역");
        stations.add(삼성역);
        stations.add(신사역);
        stations.add(사당역);

        // when
        stations1.add(new Section(삼성역, 신사역, 1));
        stations1.add(new Section(신사역, 사당역, 1));

        stations2.add(new Section(신사역, 사당역, 1));
        stations2.add(new Section(삼성역, 신사역, 1));


        // then
        assertThat(stations1.getStations()).isEqualTo(stations);
        assertThat(stations2.getStations()).isEqualTo(stations);

    }
}