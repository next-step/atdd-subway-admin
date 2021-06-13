package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {

    Station 강남역;
    Station 광교중앙역;
    Station 광교역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        광교중앙역 = new Station("광교중앙역");
        광교역 = new Station("광교역");
    }

    @DisplayName("Section이 Line에 저장되었는지 확인")
    @Test
    void checkSectionisAddedToLine() {
        //Given
        Section section = new Section(강남역, 광교중앙역, 10);

        //When
        Line line = new Line("신분당선", "red");
        section.addLine(line);

        //Then
        assertThat(line.getSections().contains(section)).isTrue();
    }

    @DisplayName("예외상황 : upStation과 downStation이 같음")
    @Test
    void checkDuplicateStation() {
        //When+Then
        assertThatThrownBy(() -> new Section(강남역, 강남역, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예외상황 : 하행역 정보 업데이트시 기존 구간의 길이보다 같거나 긺")
    @Test
    void 예외_하행역_업데이트시_추가구간_길이가_기존보다_같거나_긺() {
        //Given
        Section 기존구간 = new Section(강남역, 광교역, 30);
        Section 신규구간 = new Section(강남역, 광교중앙역, 30);

        //When+Then
        assertThatThrownBy(() -> 기존구간.updateDownStation(신규구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 역 사이의 길이와 같거나 긴 구간을 등록할 수 없습니다.");
    }

    @DisplayName("예외상황 : 상행역 정보 업데이트시 기존 구간의 길이보다 같거나 긺")
    @Test
    void 예외_상행역_업데이트시_추가구간_길이가_기존보다_같거나_긺() {
        //Given
        Section 기존구간 = new Section(강남역, 광교역, 30);
        Section 신규구간 = new Section(광교중앙역, 광교역, 40);

        //When+Then
        assertThatThrownBy(() -> 기존구간.updateUpStation(신규구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 역 사이의 길이와 같거나 긴 구간을 등록할 수 없습니다.");
    }
}
