package nextstep.subway.line.domain;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.exception.SectionNotCreateException;
import nextstep.subway.line.dto.Distance;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * packageName : nextstep.subway.line.domain
 * fileName : LineTest
 * author : haedoang
 * date : 2021/11/23
 * description :
 */
public class LineTest {
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private final int DISTANCE_10 = 10;
    private final int DISTANCE_5 = 5;

    @BeforeEach
    void setUp() {
        station1 = new Station();
        station1.setId(1L);
        station1.setName("강남역");
        station2 = new Station();
        station2.setId(2L);
        station2.setName("선릉역");
        station3 = new Station();
        station3.setId(3L);
        station3.setName("건대역");
        station4 = new Station();
        station4.setId(4L);
        station4.setName("상수역");
    }

    @Test
    @DisplayName("노선을 생성한다.")
    public void create() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));

        //then
        assertThat(line.getStations()).containsExactly(station1, station2);
    }

    @Test
    @DisplayName("노선을 상행 종점에 추가한다.")
    public void addSectionFirst() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));
        line.addSection(new Section(station3, station1, Distance.of(DISTANCE_5)));

        // then
        assertThat(line.getStations()).containsExactly(station3, station1, station2);
    }

    @Test
    @DisplayName("노선을 하행 종점에 추가한다.")
    public void addSectionLast() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));
        line.addSection(new Section(station2, station3, Distance.of(DISTANCE_5)));

        // then
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    @DisplayName("노선을 사이에 추가한다.")
    public void addSectionBetween() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));
        line.addSection(new Section(station1, station3, Distance.of(DISTANCE_5)));

        // then
        assertThat(line.getStations()).containsExactly(station1, station3, station2);
    }

    @Test
    @DisplayName("구간_등록_실패_중복")
    public void addSectionFail1() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));

        // then
        assertThatThrownBy(() -> line.addSection(new Section(station1, station2, Distance.of(DISTANCE_5))))
                .isInstanceOf(SectionNotCreateException.class)
                .hasMessageContaining("이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("구간_등록_실패_역없음")
    public void addSectionFail2() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));

        // then
        assertThatThrownBy(() -> line.addSection(new Section(station3, station4, Distance.of(DISTANCE_5))))
                .isInstanceOf(SectionNotCreateException.class)
                .hasMessageContaining("구간에 역이 존재하지 않습니다.");
    }


    @Test
    @DisplayName("구간_등록_실패_유효거리")
    public void addSectionFail3() {
        // given
        Line line = new Line("1호선", "빨강");

        // when
        line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10)));

        // then
        assertThatThrownBy(() -> line.addSection(new Section(station1, station2, Distance.of(DISTANCE_10))))
                .isInstanceOf(SectionNotCreateException.class)
                .hasMessageContaining("유효한 길이가 아닙니다.");
    }


}

