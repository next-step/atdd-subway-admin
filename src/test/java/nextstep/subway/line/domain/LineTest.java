package nextstep.subway.line.domain;

import nextstep.subway.common.exception.SectionNotCreateException;
import nextstep.subway.common.exception.SectionNotDeleteException;
import nextstep.subway.line.dto.Distance;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * packageName : nextstep.subway.line.domain
 * fileName : LineTest
 * author : haedoang
 * date : 2021/11/23
 * description :
 */
public class LineTest {
    private Station station1 = Station.of("강남역");
    private Station station2 = Station.of("선릉역");
    private Station station3 = Station.of("건대역");
    private Station station4 = Station.of("상수역");
    private final Distance DISTANCE_10 = Distance.of(10);
    private final Distance DISTANCE_5 = Distance.of(5);

    private Station 상행종점역 = Station.of("상행종점역");
    private Station 하행종점역 = Station.of("하행종점역");
    private Station 변경상행종점역 = Station.of("변경상행종점역");
    private Station 변경하행종점역 = Station.of("변경하행종점역");
    private Station 사이역 = Station.of("사이역");
    private Line 이호선 = Line.of("일호선", "빨강").addSection(Section.of(상행종점역, 하행종점역, DISTANCE_10).useId(1L));

    @BeforeEach
    void setUp() {
        station1.setId(1L);
        station2.setId(2L);
        station3.setId(3L);
        station4.setId(4L);

        상행종점역.setId(5L);
        하행종점역.setId(6L);
        변경상행종점역.setId(7L);
        변경하행종점역.setId(8L);
        사이역.setId(9L);
    }

    @Test
    @DisplayName("노선을 생성한다.")
    public void create() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));

        //then
        assertThat(line.getStations()).containsExactly(station1, station2);
    }

    @Test
    @DisplayName("노선을 상행 종점에 추가한다.")
    public void addSectionFirst() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));
        line.addSection(Section.of(station3, station1, DISTANCE_5));

        // then
        assertThat(line.getStations()).containsExactly(station3, station1, station2);
    }

    @Test
    @DisplayName("노선을 하행 종점에 추가한다.")
    public void addSectionLast() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));
        line.addSection(Section.of(station2, station3, DISTANCE_5));

        // then
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    @DisplayName("노선을 사이에 추가한다.")
    public void addSectionBetween() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));
        line.addSection(Section.of(station1, station3, DISTANCE_5));

        // then
        assertThat(line.getStations()).containsExactly(station1, station3, station2);
    }

    @Test
    @DisplayName("구간_등록_실패_중복")
    public void addSectionFail1() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));

        // then
        assertThatThrownBy(() -> line.addSection(Section.of(station1, station2, DISTANCE_5)))
                .isInstanceOf(SectionNotCreateException.class)
                .hasMessageContaining("이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("구간_등록_실패_역없음")
    public void addSectionFail2() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));

        // then
        assertThatThrownBy(() -> line.addSection(Section.of(station3, station4, DISTANCE_5)))
                .isInstanceOf(SectionNotCreateException.class)
                .hasMessageContaining("구간에 역이 존재하지 않습니다.");
    }


    @Test
    @DisplayName("구간_등록_실패_유효거리")
    public void addSectionFail3() {
        // given
        Line line = Line.of("1호선", "빨강");

        // when
        line.addSection(Section.of(station1, station2, DISTANCE_10));

        // then
        assertThatThrownBy(() -> line.addSection(Section.of(station1, station2, DISTANCE_10)))
                .isInstanceOf(SectionNotCreateException.class)
                .hasMessageContaining("유효한 길이가 아닙니다.");
    }

    @Test
    @DisplayName("노선_사이_역_제거")
    public void sectionDeleteBetween() throws Exception {
        // given
        이호선.addSection(Section.of(상행종점역, 사이역, DISTANCE_5));

        // when
        이호선.deleteSection(사이역);

        //then
        assertThat(이호선.getStations()).containsExactly(상행종점역, 하행종점역);
    }

    @Test
    @DisplayName("노선_상행역_제거")
    public void sectionDeleteFront() throws Exception {
        // given
        이호선.addSection(Section.of(상행종점역, 사이역, DISTANCE_5).useId(2L));

        // when
        이호선.deleteSection(상행종점역);

        //then
        assertThat(이호선.getStations()).containsExactly(사이역, 하행종점역);
    }

    @Test
    @DisplayName("노선_하행역_제거")
    public void sectionDeleteRear() throws Exception {
        // given
        이호선.addSection(Section.of(상행종점역, 사이역, DISTANCE_5).useId(2L));

        // when
        이호선.deleteSection(하행종점역);

        //then
        assertThat(이호선.getStations()).containsExactly(상행종점역, 사이역);
    }

    @Test
    @DisplayName("노선_삭제_unknown_역")
    public void sectionDeleteUnknownStation() throws Exception {
        // given
        이호선.addSection(Section.of(상행종점역, 사이역, DISTANCE_5).useId(2L));

        //then
        assertThatThrownBy(() -> 이호선.deleteSection(변경하행종점역))
                .isInstanceOf(SectionNotDeleteException.class)
                .hasMessageContaining("구간에 역이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("노선_삭제_구간이_1개인_경우")
    public void sectionDeleteOneSection() throws Exception {
        //then
        assertThatThrownBy(() -> 이호선.deleteSection(상행종점역))
                .isInstanceOf(SectionNotDeleteException.class)
                .hasMessageContaining("구간을 더 이상 제거할 수 없습니다.");
        assertThatThrownBy(() -> 이호선.deleteSection(하행종점역))
                .isInstanceOf(SectionNotDeleteException.class)
                .hasMessageContaining("구간을 더 이상 제거할 수 없습니다.");
    }

}

