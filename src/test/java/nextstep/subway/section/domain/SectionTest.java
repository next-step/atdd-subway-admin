package nextstep.subway.section.domain;

import nextstep.subway.exception.CannotEraseSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class SectionTest {

    @Autowired
    LineRepository lineRepository;

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 상행역 기반 ")
    void addSection() {

        // given
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Section section = new Section(line, 강남역, 정자역, 5);
        // when
        line.addNewSection(section);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 정자역, 광교역);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 하행역 기반 ")
    void addSection2() {

        // given
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Section section = new Section(line, 정자역, 광교역, 5);
        // when
        line.addNewSection(section);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 정자역, 광교역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionAtFirstBased() {
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Section section = new Section(line, 정자역, 강남역, 5);
        // when
        line.addNewSection(section);

        // then
        assertThat(line.getStations()).containsExactly(정자역, 강남역, 광교역);
    }


    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionAtLastBased() {
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Section section = new Section(line, 광교역, 정자역, 5);
        // when
        line.addNewSection(section);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 광교역, 정자역);
    }

    @DisplayName("노선에 구간의 길이보다 긴 거리로 등록하면 등록할 수 없는 테스트")
    @Test
    void addSectionGreaterDistance() {
        // given
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Section section = new Section(line, 강남역, 정자역, 11);

        assertThatThrownBy(() -> line.addNewSection(section))
                .isInstanceOf(RuntimeException.class);

    }


    @DisplayName("노선에 이미 등록된 역들로 구간을 등록할 수 없는 테스트")
    @Test
    void addAlreadyExistStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Section section = new Section(line, 강남역, 광교역, 5);

        assertThatThrownBy(() -> line.addNewSection(section))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("노선에 등록된 역이 하나도 없는 구간은 등록할 수 없는 테스트")
    @Test
    void addNoExistStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        Station 새로운역 = new Station("새로운역");
        Station 새로운역2 = new Station("새로운역2");
        Section section = new Section(line, 새로운역, 새로운역2, 11);

        assertThatThrownBy(() -> line.addNewSection(section))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("노선에 구간에 사이를 제거한다.")
    @Test
    void deleteMiddleSection() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 정자역 = new Station(2L, "정자역");
        Station 광교역 = new Station(3L, "광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);
        Section section = new Section(line, 강남역, 정자역, 5);
        // when
        line.addNewSection(section);

        line.deleteStation(정자역.getId());

        // then
        assertThat(line.getStations()).containsExactly(강남역, 광교역);
    }


    @DisplayName("노선에 구간에 상향을  제거한다.")
    @Test
    void deleteStartSection() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 정자역 = new Station(2L, "정자역");
        Station 광교역 = new Station(3L, "광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);
        Section section = new Section(line, 강남역, 정자역, 5);
        // when
        line.addNewSection(section);

        line.deleteStation(강남역.getId());

        // then
        assertThat(line.getStations()).containsExactly(정자역, 광교역);
    }

    @DisplayName("노선에 구간에 하향을  제거한다.")
    @Test
    void deleteLastSection() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 정자역 = new Station(2L, "정자역");
        Station 광교역 = new Station(3L, "광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);
        Section section = new Section(line, 강남역, 정자역, 5);
        // when
        line.addNewSection(section);

        line.deleteStation(광교역.getId());

        // then
        assertThat(line.getStations()).containsExactly(강남역, 정자역);
    }

    @DisplayName("노선에 등록되지 않은 역은 제거할 수 없다")
    @Test
    void deleteNoExistStations() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 정자역 = new Station(2L, "정자역");
        Station 광교역 = new Station(3L, "광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);
        Section section = new Section(line, 강남역, 정자역, 5);
        // when
        line.addNewSection(section);

        Station 신사역 = new Station(4L, "신사역");
        assertThatThrownBy(() -> line.deleteStation(신사역.getId()))
                .isInstanceOf(CannotEraseSectionException.class)
                .hasMessage("해당 노선은 제거할 수 없습니다.");
    }

    @DisplayName("노선에 등록된 구간이 1개일 때, 상행역을 제거할 수 없다")
    @Test
    void deleteUpStationInOnlyOneSections() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 정자역 = new Station(2L, "정자역");
        Station 광교역 = new Station(3L, "광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        assertThatThrownBy(() -> line.deleteStation(강남역.getId()))
                .isInstanceOf(CannotEraseSectionException.class)
                .hasMessage("해당 노선은 제거할 수 없습니다.");
    }


    @DisplayName("노선에 등록된 구간이 1개일 때, 하행역을 제거할 수 없다")
    @Test
    void deleteDownStationInOnlyOneSections() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 정자역 = new Station(2L, "정자역");
        Station 광교역 = new Station(3L, "광교역");

        Line line = new Line("신분당선", "red", 강남역, 광교역, 10);

        assertThatThrownBy(() -> line.deleteStation(광교역.getId()))
                .isInstanceOf(CannotEraseSectionException.class)
                .hasMessage("해당 노선은 제거할 수 없습니다.");
    }
}
