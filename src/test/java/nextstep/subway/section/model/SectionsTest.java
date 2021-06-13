package nextstep.subway.section.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.section.NotFoundSectionException;
import nextstep.subway.exception.section.NotPossibleRemoveException;
import nextstep.subway.exception.station.StationsAlreadyExistException;
import nextstep.subway.exception.station.StationsNoExistException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class SectionsTest {

    private Sections sections;
    private Station upStation;
    private Station downStation;
    private Station newStation;
    private Line line;
    private Station newStation2;
    private Section sectionFirst;

    @BeforeEach
    void setup() {
        upStation = Station.of(1L, "삼성역");
        downStation = Station.of(2L, "교대역");
        newStation = Station.of(3L, "선릉역");
        newStation2 = Station.of(4L, "전장역");
        line = new Line(1L, "2호선", "green");
        sectionFirst = Section.of(1L, upStation, downStation, line, 20);
        sections = Sections.of(new ArrayList<Section>(Arrays.asList(sectionFirst)));
    }

    @Test
    @DisplayName("두 역 사이 거리 얻어오는 것 테스트")
    void getDistanceWithStationsTest() {
        assertThat(sections.getDistanceWithStations(upStation, downStation)).isEqualTo(20);
        assertThrows(NotFoundSectionException.class, () -> sections.getDistanceWithStations(upStation, newStation));

    }

    @Test
    @DisplayName("stations 상행 > 하행선으로 가져오는 것 테스트")
    void toResponseListTest() {
        List<StationResponse> stationList = sections.toResponseList();
        assertThat(stationList.size()).isEqualTo(2);
        assertThat(stationList.get(0).getName()).isEqualTo(upStation.getName());
        assertThat(stationList.get(1).getName()).isEqualTo(downStation.getName());

    }

    @Test
    @DisplayName("신규 구간 등록 :( A - B ) + ( B + C ) => (A - B - C)")
    void addTest() {
        Section section = Section.of(2L, downStation, newStation, line, 10);
        sections.add(section);
        List<String> sationNames = sections.toResponseList().stream().map(response -> response.getName())
            .collect(Collectors.toList());
        assertThat(sationNames).containsExactly(upStation.getName(), downStation.getName(), newStation.getName());
        assertThat(sections.getDistanceWithStations(upStation, downStation)).isEqualTo(20);
        assertThat(sections.getDistanceWithStations(downStation, newStation)).isEqualTo(10);
    }

    @Test
    @DisplayName("신규 구간 등록 :( A - B ) + ( C - A ) => ( C - A - B )")
    void addTest2() {
        Section section = Section.of(2L, newStation, upStation, line, 10);
        sections.add(section);
        List<String> sationNames = sections.toResponseList().stream().map(response -> response.getName())
            .collect(Collectors.toList());
        assertThat(sationNames).containsExactly(newStation.getName(), upStation.getName(), downStation.getName());
        assertThat(sections.getDistanceWithStations(upStation, downStation)).isEqualTo(20);
        assertThat(sections.getDistanceWithStations(newStation, upStation)).isEqualTo(10);
    }

    @Test
    @DisplayName("신규 구간 등록 : (A - B(20)) + (A - C(10)) + (D + C(5)) ) + => (A - C(10) - D(5) - B(5))")
    void addTest3() {
        Section section = Section.of(2L, upStation, newStation, line, 10);
        sections.add(section);
        section = Section.of(3L, newStation2, downStation, line, 5);
        sections.add(section);
        List<String> sationNames = sections.toResponseList().stream().map(response -> response.getName())
            .collect(Collectors.toList());
        assertThat(sationNames).containsExactly(upStation.getName(), newStation.getName(), newStation2.getName(),
            downStation.getName());
        assertThat(sections.getDistanceWithStations(upStation, newStation)).isEqualTo(10);
        assertThat(sections.getDistanceWithStations(newStation, newStation2)).isEqualTo(5);
        assertThat(sections.getDistanceWithStations(newStation2, downStation)).isEqualTo(5);
    }

    @Test
    @DisplayName("기존에 등록되어 있거나, 없는 역은 에러를 발생시킨다.")
    void alreadyOrNoExistTest() {
        assertThrows(StationsAlreadyExistException.class, () -> {
            Section section = Section.of(2L, upStation, downStation, line, 10);
            sections.add(section);
        });

        assertThrows(StationsNoExistException.class, () -> {
            Section section = Section.of(2L, newStation, newStation2, line, 10);
            sections.add(section);
        });
    }

    @Test
    @DisplayName("A-B-C 중 A 삭제 테스트")
    void removeAB() {
        Section section = Section.of(2L, upStation, newStation, line, 10);
        sections.add(section);
        sections.remove(upStation);
        assertThat(sections.getDistanceWithStations(newStation, downStation)).isEqualTo(10);

    }

    @Test
    @DisplayName("A-B-C 중 B 삭제 테스트")
    void removeBC() {
        Section section = Section.of(2L, downStation, newStation, line, 10);
        sections.add(section);
        sections.remove(downStation);
        assertThat(sections.getDistanceWithStations(upStation, newStation)).isEqualTo(30);
    }

    @Test
    @DisplayName("A-B-C-D 중 B 삭제 테스트")
    void removeBDInner() {
        Section sectionBC = Section.of(2L, downStation, newStation, line, 10);
        sections.add(sectionBC);
        Section sectionCD = Section.of(2L, newStation, newStation2, line, 10);
        sections.add(sectionCD);

        sections.remove(downStation);
        assertThat(sections.getDistanceWithStations(upStation, newStation)).isEqualTo(30);
    }

    @Test
    @DisplayName("구간이 1개밖에 없을 때는 삭제가 불가능 하다.")
    void removeOnlyOne() {
        assertThrows(NotPossibleRemoveException.class, () -> {
            sections.remove(upStation);
        });
    }

    @Test
    @DisplayName("존재하지 않는 구간은 삭제 불가능 하다.")
    void removeNoSection() {
        assertThrows(NotPossibleRemoveException.class, () -> {
            Section.of(2L, upStation, newStation, line, 10);
            sections.remove(newStation);
        });
    }

}
