package nextstep.subway.section;

import org.junit.jupiter.api.BeforeEach;

public class SectionsTest {

    @BeforeEach
    void setUp() {

    }

    /*@DisplayName("구간들 중에 해당 역이 있는지 확인")
    @Test
    public void 구간들중에_역존재확인() throws Exception {
        //given
        Station upStation = Station.create("방화역");
        Station downStation = Station.create("하남검단산역");
        Line line = Line.create("5호선", "보라색", upStation, downStation, 1000);
        Sections sections = line.sections();

        //when
        //then
        assertThat(sections.isExistStation(upStation)).isTrue();
    }

    @DisplayName("구간생성 - 두 개의 역이 이미 존재하는 경우 예외 발생")
    @Test
    public void 두개의역이이미존재하는경우_구간생성시_예외발생() throws Exception {
        //given
        Station upStation = Station.create("방화역");
        Station downStation = Station.create("하남검단산역");
        Line line = Line.create("5호선", "보라색", upStation, downStation, 1000);

        //when
        //then
        assertThatThrownBy(() -> Section.create(line, upStation, downStation, 10))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("구간생성 - 두 개의 역이 모두 존재하지 않는 경우 예외 발생")
    @Test
    public void 두개의역이모두존재하는않는경우_구간생성시_예외발생() throws Exception {
        //given
        Station upStation = Station.create("방화역");
        Station downStation = Station.create("하남검단산역");
        Line line = Line.create("5호선", "보라색", upStation, downStation, 1000);
        Station newUpStation = Station.create("새로운상행역");
        Station newDownStation = Station.create("새로운하행역");

        //when
        //then
        assertThatThrownBy(() -> Section.create(line, newUpStation, newDownStation, 10))
                .isInstanceOf(IllegalStateException.class);
    }*/

    /*@DisplayName("노선에 역 추가 등록 - 기존에 있는 역이 하나만 존재 - 기존역이 상행종점역이고 기존역을 하행역으로 만드는 경우")
    @Test
    public void 역을노선에추가등록시_등록확인() throws Exception {
        //given
        Station newStation = new Station(3L, "새로운역");

        //when
        line.connect(upStation, DOWN, newStation);

        //then
        assertThat(line.stationsFromUpToDown()).containsExactly(newStation, upStation, downStation);
    }*/
}
