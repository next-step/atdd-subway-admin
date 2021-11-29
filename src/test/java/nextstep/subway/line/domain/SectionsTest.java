package nextstep.subway.line.domain;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.line.domain.Sections.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.ServiceException;
import nextstep.subway.station.domain.Station;

class SectionsTest {

    /**
     * 구파발역 ->  신촌역 -> 대화역 -> 용문역 -> 강남역
     */
    @Test
    void 지하철_상행_하행_에따른_노선_정렬_확인() {
        Line line = new Line("1호선", "red");
        Section section2 = new Section(new Station(55L, "대화역"), new Station(3L,"용문역"), SectionType.MIDDLE, new Distance(10));
        Section section5 = new Section(new Station(5L, "신촌역"), new Station(55L, "대화역"), SectionType.MIDDLE, new Distance(10));
        Section section1 = new Section(new Station(2L, "구파발역"), new Station(5L, "신촌역"), SectionType.FIRST, new Distance(10));
        Section lastSection = new Section(new Station(2L, 강남역), null, SectionType.LAST, new Distance(10));
        Section section4 = new Section(new Station(3L, "용문역"), new Station(2L, 강남역), SectionType.MIDDLE, new Distance(5));
        line.createSection(asList(section4, section1, section5, section2, lastSection));

        List<Station> sections = line.getStations();

        List<String> collect = sections.stream()
                                     .map(Station::getName)
                                     .collect(toList());

        assertThat(collect).containsExactly("구파발역","신촌역","대화역","용문역",강남역);
    }

    /**
     * 첫번째역은 강남역
     */
    @Test
    void 지하철라인의_가장_첫역을_찾는다() {

        // given
        Line line = new Line("1호선", "red");
        Section firstSection = new Section(new Station(2L, 강남역), new Station(5L, "신촌역"), SectionType.FIRST, new Distance(10));
        Section nextSection = new Section(new Station(5L, "신촌역"), new Station(55L, "대화역"), SectionType.MIDDLE, new Distance(10));

        Sections sections = new Sections();
        sections.add(asList(firstSection, nextSection), line);

        // when
        Section findSection = sections.findFirstSection();

        // then
        assertThat(findSection).isEqualTo(firstSection);
    }

    /**
     * 강남역의 다음역은 신촌역
     */
    @Test
    void 다음_구간을_찾는다() {
        // given
        Section firstSection = new Section(new Station(2L, 강남역), new Station(5L, "신촌역"), SectionType.FIRST, new Distance(10));
        Section nextSection = new Section(new Station(5L, "신촌역"), new Station(55L, "대화역"), SectionType.MIDDLE, new Distance(10));

        Sections sections = new Sections();
        Line line = new Line();
        sections.add(asList(firstSection, nextSection), line);

        // when
        List<Station> resultStation = sections.getStations();

        // then
        List<Long> stationIds = resultStation.stream()
                                             .map(Station::getId)
                                             .collect(toList());
        assertThat(stationIds).containsExactly(2L, 5L);
    }

    @Test
    void 상행역을_등록한다() {
        //given
        Line line = new Line(LINE_ONE, LINE_ONE_COLOR_RED);
        Station stationGangnam = new Station(1L, 강남역);
        Station stationSinChon = new Station(2L, "신촌역");
        Section section = new Section(stationGangnam, stationSinChon, SectionType.FIRST, new Distance(10));
        Sections sections = new Sections();
        sections.add(section, line);

        // when
        Station stationYoungSan = new Station(3L, "용산역");
        sections.addSection(new Section(stationYoungSan, stationGangnam, SectionType.MIDDLE, new Distance(4), line));


        // then
        Sections expectedSections = new Sections();
        List<Section> sections1 = asList(
            new Section(stationGangnam, stationSinChon, SectionType.MIDDLE, new Distance(10)),
            new Section(stationYoungSan, stationGangnam, SectionType.FIRST, new Distance(4))
        );

        expectedSections.add(sections1, line);

        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }

    @Test
    void 하행역을_등록한다() {
        //given
        Line line = new Line(LINE_ONE, LINE_ONE_COLOR_RED);
        Station stationGangnam = new Station(1L, "강남역");
        Station stationSinChon = new Station(2L, "신촌역");
        Section firstSection = new Section(stationGangnam, stationSinChon, SectionType.FIRST, new Distance(10));
        Section lastSection = new Section(stationSinChon, null, SectionType.LAST, new Distance(0));
        Sections sections = new Sections();
        sections.add(firstSection, line);
        sections.add(lastSection, line);

        // when
        Station stationYoungSan = new Station(3L, "용산역");
        sections.addSection(new Section(stationSinChon, stationYoungSan, SectionType.MIDDLE, new Distance(4), line));


        // then
        Sections expectedSections = new Sections();
        List<Section> sections1 = asList(
            new Section(stationGangnam, stationSinChon, SectionType.FIRST, new Distance(10)),
            new Section(stationYoungSan, null, SectionType.LAST, new Distance(0)),
            new Section(stationSinChon, stationYoungSan, SectionType.MIDDLE, new Distance(4))
        );

        expectedSections.add(sections1, line);

        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }


    @Test
    void 등록되지_않은_구간_추가시_예외() {
        //given
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(1L, "신촌역");

        Station addStation1 = new Station(1L, "서울역");
        Station addStation2= new Station(1L, "용산역");

        // when
        Sections sections = new Sections();
        Section section = new Section(station1, station2, SectionType.MIDDLE, new Distance(10));
        sections.add(section, new Line(LINE_ONE, LINE_ONE_COLOR_RED));

        // then
        Assertions.assertThatThrownBy(() -> {
                      sections.addSection(new Section(addStation1, addStation2, SectionType.MIDDLE, new Distance(5)));
                  }).isInstanceOf(ServiceException.class)
                  .hasMessageStartingWith("기존 노선에 해당역이 존재하지 않습니다.");
    }

}