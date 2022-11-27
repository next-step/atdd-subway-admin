package nextstep.subway.section;

import nextstep.subway.station.Station;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 목록 도메인 테스트")
class SectionsTest {

    private Station 강남;
    private Station 양재;
    private Sections sections;

    @BeforeEach
    void init() {
        sections = new Sections();
        강남 = new Station("강남");
        양재 = new Station("양재");
        Section 강남_양재_구간 = new Section(강남, 양재, new Distance(50L));
        sections.add(강남_양재_구간);
    }

    @AfterEach
    void printResult() {
        System.out.println(sections.toString());
    }

    @DisplayName("구간이 등록된 경우 정보 목록 조회")
    @Test
    void getStations() {
        Station 광교중앙 = new Station("광교중앙영");
        Section 양재_광교중앙_구간 = new Section(양재, 광교중앙, new Distance(30L));
        sections.add(양재_광교중앙_구간);
        assertThat(sections.getStationsResponse().size()).isEqualTo(3);
    }

    @DisplayName("구간이 등록되지 않은 경우 정보 목록 조회")
    @Test
    void getEmptyStations() {
        Sections sections = new Sections();
        assertThat(sections.getStationsResponse().size()).isEqualTo(0);
    }

    @DisplayName("새로운 상행 종점 등록")
    @Test
    void extendUpStation() {
        Station 신사 = registerUpSection(강남, "신사", 10L);
        assertStationNamesToUp(신사);
    }

    @DisplayName("새로운 하행 종점 등록")
    @Test
    void extendDownStation() {
        Station 광교중앙 = registerDownSection(양재, "광교중앙", 30L);
        assertStationNamesToDown(광교중앙);
    }

    @DisplayName("하행 구간 사이 새로운 구간 등록 (시작으로부터 1구간 이내)")
    @Test
    void sliceDownSection1() {
        Station 수지구청 = registerDownSection(양재, "수지구청", 50L);
        Station 광교중앙 = registerDownSection(수지구청, "광교중앙", 30L);
        Station 동천 = registerDownSection(양재, "동천", 40L);
        assertStationNamesToDown(동천, 수지구청, 광교중앙);
    }

    @DisplayName("하행 구간 사이 새로운 구간 등록 (시작으로부터 2구간 이내")
    @Test
    void sliceDownSection2() {
        Station 동천 = registerDownSection(양재, "동천", 40L);
        Station 수지구청 = registerDownSection(동천, "수지구청", 10L);
        Station 광교중앙 = registerDownSection(수지구청, "광교중앙", 30L);
        Station 상현 = registerDownSection(양재, "상현", 65L);
        assertStationNamesToDown(동천, 수지구청, 상현, 광교중앙);
    }

    @DisplayName("상행 구간 사이 새로운 구간 등록 (시작으로부터 1구간 이내)")
    @Test
    void sliceUpSection1() {
        Station 동천 = registerDownSection(양재, "동천", 40L);
        Station 수지 = registerDownSection(동천, "수지", 10L);
        Station 판교 = registerUpSection(동천, "판교", 30L);
        assertStationNamesToDown(판교, 동천, 수지);
    }

    @DisplayName("상행 구간 사이 새로운 구간 등록 (시작으로부터 2구간 이내)")
    @Test
    void sliceUpSection2() {
        Station 동천 = registerDownSection(양재, "동천", 40L);
        Station 수지 = registerDownSection(동천, "수지", 10L);
        Station 판교 = registerUpSection(수지, "판교", 40L);
        assertStationNamesToDown(판교, 동천, 수지);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 오류 발생")
    @Test
    void duplicateStationError() {
        Station 강남 = new Station("강남");
        Station 양재 = new Station("양재");
        Section 강남_양재_구간 = new Section(강남, 양재, new Distance(50L));

        assertThatThrownBy(() -> {
            sections.add(강남_양재_구간);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 구간의 역은 모두 이미 등록되었습니다.");
    }

    @DisplayName("상행역, 하행역 둘 모두 포함되지 않은 경우 등록 오류 발생")
    @Test
    void notRegisteredStationError() {
        Station 판교 = new Station("판교");
        Station 상현 = new Station("상현");
        Section 판교_상현_구간 = new Section(판교, 상현, new Distance(30L));

        assertThatThrownBy(() -> {
            sections.add(판교_상현_구간);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 구간의 두 역 중 하나 이상은 등록되어 있어야 합니다.");
    }

    @DisplayName("역 사이 새로운 역을 등록하는 경우 기존 역 사이 길이와 같으면 오류 발생")
    @Test
    void alreadyLocatedStationError() {
        Station 강남 = new Station("강남");
        Station 판교 = new Station("판교");
        Section 강남_판교_구간 = new Section(강남, 판교, new Distance(50L));

        assertThatThrownBy(() -> {
            sections.add(강남_판교_구간);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 거리에 이미 역이 등록되었습니다.");
    }

    @DisplayName("역 사이 새로운 역을 등록하는 경우 기존 역 사이보다 크면 오류 발생")
    @Test
    void exceedStationError() {
        Station 강남 = new Station("강남");
        Station 판교 = new Station("판교");
        Section 강남_판교_구간 = new Section(강남, 판교, new Distance(60L));

        assertThatThrownBy(() -> {
            sections.add(강남_판교_구간);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 역의 길이가 종점까지의 거리를 초과했습니다.");
    }

    private List<String> getStationNames() {
        List<Map<String, Object>> stations = sections.getStationsResponse();
        return stations.stream()
                .map(station -> station.get("name").toString())
                .collect(Collectors.toList());
    }

    private Station registerDownSection(Station upStation, String downStationName, Long distance) {
        Station downStation = new Station(downStationName);
        Section section = new Section(upStation, downStation, new Distance(distance));
        sections.add(section);
        return downStation;
    }

    private Station registerUpSection(Station downStation, String upStationName, Long distance) {
        Station upStation = new Station(upStationName);
        Section section = new Section(upStation, downStation, new Distance(distance));
        sections.add(section);
        return upStation;
    }

    private void assertStationNamesToDown(Station... stations) {
        List<String> names = new ArrayList<>(Arrays.asList(강남.getName(), 양재.getName()));
        Arrays.stream(stations).forEach(station -> names.add(station.getName()));
        String[] collect = names.toArray(new String[0]);
        assertThat(getStationNames()).containsExactly(collect);
    }

    private void assertStationNamesToUp(Station... stations) {
        List<String> names = Arrays.stream(stations)
                .map(Station::getName)
                .collect(Collectors.toList());
        Collections.reverse(names);
        names.add(강남.getName());
        names.add(양재.getName());
        String[] collect = names.toArray(new String[0]);
        assertThat(getStationNames()).containsExactly(collect);
    }
}