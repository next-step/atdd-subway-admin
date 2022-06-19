//package nextstep.subway.domain.line;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.ArrayList;
//import nextstep.subway.domain.station.Station;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//// 추가하려는 구간이 이미 존재하는 구간인 경우 예외
//// 구간 길이가 크거나 같은경우 예외
//// 상행역이 서로 같고 하행역이 서로 다른 경우 -> 중간에 추가
//// 상행역이 서로 다르고 하행역이 서로 같은 경우 -> 중간에 추가
//// 상행역이 서로 다르고 요청 하행역이 현재 상행역과 같은경우 -> 상행역 교체
//// 하행역이 서로 다르고 요청 하행역이 현재 상행역이 같은경우 -> 하행역 교체
//class SectionsTest {
//
//    private Line line;
//    private Station finalUpStation;
//    private Station finalDownStation;
//    private int distance = 10;
//
//    @BeforeEach
//    void setUp() {
//        finalUpStation = new Station("신사역");
//        finalDownStation = new Station("신논현역");
//        line = new Line("신분당선", "red", finalUpStation, finalDownStation, distance);
//        assertThat(line.getAllSections()).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("추가하려는 구간이 이미 존재하는 구간인 경우 예외")
//    public void throwException_WhenContainsSameSection(){
//        // Given
//
//        Station upStation = new Station("신사역");
//        Station downStation = new Station("신논현역");
//        int distance = 10;
//
//        Section given = new Section(line, upStation, downStation, 10);
//
//        // When
//        Sections sections = new Sections(new ArrayList<>());
//        sections.add(given);
//
//        // Then
//        assertThat(sections.size()).isEqualTo(1);
//    }
//}
