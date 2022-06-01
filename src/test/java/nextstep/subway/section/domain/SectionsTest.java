package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    public static final int DEFAULT_DISTANCE = 10;
    private Station 상행역;
    private Station 하행역;
    private Station 신규역;
    private Section defaultSection;

    @BeforeEach
    void initialize(){
        상행역 = new Station("first");
        하행역 = new Station("last");
        신규역 = new Station("new");
        defaultSection = new Section(상행역, 하행역, DEFAULT_DISTANCE);
    }

    @Test
    @DisplayName("Sections에 존재하는 Stations 조회")
    void Stations_조회(){
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));
        List<Station> stations = sections.getSortedStations();
        assertThat(stations).containsExactly(상행역, 하행역);
    }

    @Test
    @DisplayName("기존 존재하는 구간 사이에 새로운 구간 등록 : [상행역 - 신규역] 구간 등록")
    void 상행역_신규역_구간_등록(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));
        sections.addSection(new Section(상행역, 신규역, 5));

        //when
        List<Station> sortedStations = sections.getSortedStations();

        //then
        assertThat(sortedStations).containsExactly(상행역, 신규역, 하행역);
    }

    @Test
    @DisplayName("기존 존재하는 구간 사이에 새로운 구간 등록 : [신규역 - 하행역] 구간 등록")
    void 신규역_하행역_구간_등록(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));
        sections.addSection(new Section(신규역, 하행역, 5));

        //when
        List<Station> sortedStations = sections.getSortedStations();

        //then
        assertThat(sortedStations).containsExactly(상행역, 신규역, 하행역);
    }

    @Test
    @DisplayName("새로운 상행종점 구간 등록 : [신규역 - 상행역] 구간 등록")
    void 신규역_상행역_구간_등록(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));
        sections.addSection(new Section(신규역, 상행역, 5));

        //when
        List<Station> sortedStations = sections.getSortedStations();

        //then
        assertThat(sortedStations).containsExactly(신규역, 상행역, 하행역);
    }

    @Test
    @DisplayName("새로운 하행종점 구간 등록 : [하행역 - 신규역] 구간 등록")
    void 하행역_신규역_구간_등록(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));
        sections.addSection(new Section(하행역, 신규역, 5));

        //when
        List<Station> sortedStations = sections.getSortedStations();

        //then
        assertThat(sortedStations).containsExactly(상행역, 하행역, 신규역);
    }

    @Test
    @DisplayName("예외처리 : 역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다")
    void Exception_Invalid_Distance(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));

        //then
        assertThrows(IllegalArgumentException.class,
                () -> sections.addSection(new Section(상행역, 신규역, DEFAULT_DISTANCE)));
    }

    @Test
    @DisplayName("예외처리 : 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    void Exception_Already_Exist_Section(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));

        //then
        assertThrows(IllegalArgumentException.class,
                () -> sections.addSection(new Section(상행역, 하행역, 5)));

    }

    @Test
    @DisplayName("예외처리 : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    void Exception_Unknown_Stations(){
        //given
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(defaultSection)));
        Station 신규역2 = new Station("new2");

        //then
        assertThrows(IllegalArgumentException.class,
                () -> sections.addSection(new Section(신규역, 신규역2, 5)));

    }
}