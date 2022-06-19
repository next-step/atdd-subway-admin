//package nextstep.subway.domain.line;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import nextstep.subway.domain.station.Station;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//// 추가하려는 구간이 이미 존재하는 구간인 경우 예외
//// 상행역이 서로 같고 하행역이 서로 다른 경우 -> 중간에 추가
//// 상행역이 서로 다르고 하행역이 서로 같은 경우 -> 중간에 추가
//// 상행역이 서로 다르고 요청 하행역이 현재 상행역과 같은경우 -> 상행역 교체
//// 하행역이 서로 다르고 요청 하행역이 현재 상행역이 같은경우 -> 하행역 교체
//class SectionTest {
//
//    @Test
//    @DisplayName("동일 구간 여부 검증")
//    public void isSameStation(){
//        // Given
//        Line line = new Line("신분당선", "red");
//
//        Station upStation = new Station("신사역");
//        Station downStation = new Station("신논현역");
//        Station newDownStation = new Station("강남역");
//        int distance = 10;
//
//        // When
//        Section section = new Section(line, upStation, downStation, 10);
//        Section newSection = new Section(line, upStation, downStation, 10);
//
//        // Then
//        assertThat(section.isSameSection(newSection)).isTrue();
//    }
//
//    @Test
//    @DisplayName("동일 구간이 존재하는 경우 예외 발생 검증")
//    public void throwException_WhenAddSameSection(){
//        // Given
//
//        // When
//
//        // Then
//    }
//}
