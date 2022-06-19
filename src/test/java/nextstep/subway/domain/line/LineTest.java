//package nextstep.subway.domain.line;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//import nextstep.subway.domain.station.Station;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//@DisplayName("Domain:Line")
//class LineTest {
//
////    private Line line;
////    private Station finalUpStation;
////    private Station finalDownStation;
////
////    private Line generateLine() {
////        line = new Line("신분당선", "red");
////        finalUpStation = new Station("신사역");
////        finalDownStation = new Station("정자역");
////        int distance = 10;
////        line.addSection(finalUpStation, finalDownStation, distance);
////        return line;
////    }
//
//    @Test
//    @DisplayName("노선에 구간을 추가한다.")
//    public void addSection() {
//        // Given
//        Line line = new Line("신분당선", "red");
//        Station upStation = new Station("신사역");
//        Station downStation = new Station("정자역");
//        int distance = 10;
//
//        // When
//        line.addSection(upStation, downStation, distance);
//
//        // Then
//        assertThat(line.getFinalUpStation()).isEqualTo(upStation);
//        assertThat(line.getFinalDownStation()).isEqualTo(downStation);
//        assertThat(line.getAllSections()).hasSize(1);
//        assertThat(line.getAllStations()).containsExactly(upStation, downStation);
//    }
//
//
//    @Test
//    @DisplayName("노선 중간에 구간을 추가한다.")
//    public void addSectionInMiddle() {
//        // Given
//        Line line = new Line("신분당선", "red");
//        Station finalUpStation = new Station("신사역");
//        Station finalDownStation = new Station("정자역");
//        int distance = 10;
//        line.addSection(finalUpStation, finalDownStation, 10);
//
//        Station upStation = new Station("신사역");
//        Station downStation = new Station("신논현역");
//        distance = 5;
//
//        // When
//        line.addSection(upStation, downStation, 5);
//
//        // Then
//        assertAll(
//            () -> assertThat(line.getAllSections()).hasSize(2),
////            () -> assertThat(line.getFinalUpStation().getName()).isEqualTo(finalUpStation.getName()),
////            () -> assertThat(line.getFinalDownStation().getName()).isEqualTo(finalDownStation.getName()),
//            () -> assertThat(line.getAllStations()).containsExactly(finalUpStation, downStation, finalDownStation)
//        );
//    }
//
//
//    @Test
//    @DisplayName("노선의 상행종점역을 조회한다.")
//    public void findUpStation() {
//        // Given
//        Line line = new Line("신분당선", "red");
//
//        // When
//
//        // Then
//    }
//
//    @Test
//    @DisplayName("노선의 하행종점역을 조회한다.")
//    public void findDownStation() {
//        // Given
//
//        // When
//
//        // Then
//    }
//
//    @Test
//    @DisplayName("노선에 포함된 지하철 역 목록을 조회한다.")
//    public void getStations() {
//        // Given
//
//        // When
//
//        // Then
//    }
//
//    @Test
//    @DisplayName("노선의 신규 상행종점역 구간을 추가한다.")
//    public void addNewUpStationSection() {
//        // Given
//
//        // When
//
//        // Then
//    }
//
//    @Test
//    @DisplayName("노선의 신규 하행종점역 구간을 추가한다.")
//    public void addNewDownStationSection() {
//        // Given
//
//        // When
//
//        // Then
//    }
//
//    @Test
//    @DisplayName("노선의 구간 목록을 조회한다.")
//    public void getSections() {
//        // Given
//
//        // When
//
//        // Then
//    }
//
//    @Test
//    @DisplayName("노선의 전체 길이를 조회한다.")
//    public void getDistance() {
//        // Given
//
//        // When
//
//        // Then
//    }
//}
