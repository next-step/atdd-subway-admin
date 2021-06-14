package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철 노선 관련 기능 객체 테스트")
public class LineTest {
    private Line 신분당선;
    private Station 양재역;
    private Station 수지구청역;
    private Station 양재숲역;
    private Station 판교역;
    private Station 정자역;
    private Station 광교역;
    private Station 강남역;

    @BeforeEach
    void setUp() {
        노선_생성();
        강남역 = new Station("강남역");
        양재숲역 = new Station("양재숲역");
        판교역 = new Station("판교역");
        정자역 = new Station("정자역");
        광교역 = new Station("광교역");
    }

    @DisplayName("노선에 구간 추가하기")
    @Test
    void addSections() {
        구간_등록(강남역, 양재역, 30);
        구간_등록(양재역, 판교역, 20);

        List<Station> result = 신분당선.getStations();

        assertThat(result).contains(강남역);
        assertThat(result).contains(판교역);
    }

    @DisplayName("노선에 구간 여러개 추가하기")
    @Test
    void addSections2() {
        구간_등록(강남역, 양재역, 30);
        구간_등록(양재역, 판교역, 20);

        List<Station> result = 신분당선.getStations();

        입력한_모든_역이_존재함(result);
        입력한_역이_정렬됨(result);
    }

    @DisplayName("상/하행선이 동일한 구간 추가 불가")
    @Test
    void addDuplicationSection() {
        Section 양재_수지구청역 = new Section(신분당선, 양재역, 수지구청역, 30);
        boolean isUpStationExist = 신분당선.isStationExist(양재_수지구청역.getUpStation());
        boolean isDownStationExist = 신분당선.isStationExist(양재_수지구청역.getDownStation());

        assertThatThrownBy(() -> {
            신분당선.validSection(isUpStationExist, isDownStationExist);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("동일한 구간은 추가할 수 없습니다.");
    }

    @DisplayName("상/하행선 둘중 하나와도 일치하지 않는 구간 추가 불가")
    @Test
    void addSameOneSection() {
        Section 양재_수지구청역 = new Section(신분당선, 양재숲역, 판교역, 30);
        boolean isUpStationExist = 신분당선.isStationExist(양재_수지구청역.getUpStation());
        boolean isDownStationExist = 신분당선.isStationExist(양재_수지구청역.getDownStation());

        assertThatThrownBy(() -> {
            신분당선.validSection(isUpStationExist, isDownStationExist);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("상/하행선 둘 중 하나는 일치해야 합니다.");
    }

    @DisplayName("중간에 구간 추가시 기존 구간보다 긴 거리값 가지고 있을 경우 추가 불가")
    @Test
    void checkDistance() {
        assertThatThrownBy(() -> {
            구간_등록(양재역, 판교역, 400);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("기존 구간보다 긴 거리값은 추가할수 없습니다.");
    }

    private void 노선_생성() {
        양재역 = new Station("양재역");
        수지구청역 = new Station("수지구청역");
        신분당선 = new Line("신분당선", "red", 양재역, 수지구청역, 300);
    }

    private void 구간_등록(Station upStation, Station downStationName, int distance) {
        Section section = new Section(신분당선, upStation, downStationName, distance);
        신분당선.addSection(section);
    }

    private void 입력한_모든_역이_존재함(List<Station> result) {
        List<Station> allInputStation = new ArrayList<>();
        allInputStation.add(판교역);
        allInputStation.add(수지구청역);
        allInputStation.add(강남역);
        allInputStation.add(양재역);
        assertThat(result).containsAll(allInputStation);
    }

    private void 입력한_역이_정렬됨(List<Station> result) {
        List<Station> allInputStation = new ArrayList<>();
        allInputStation.add(강남역);
        allInputStation.add(양재역);
        allInputStation.add(판교역);
        allInputStation.add(수지구청역);
        assertThat(result).containsExactlyElementsOf(allInputStation);
    }
}
