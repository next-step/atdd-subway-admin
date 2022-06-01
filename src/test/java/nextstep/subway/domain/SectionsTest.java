package nextstep.subway.domain;

import static nextstep.subway.domain.Station.createStation;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class SectionsTest {
    Sections sections;

    @BeforeEach
    void setUp() {
        final Section initSection = Section.builder()
                .distance(10)
                .upStation(createStation("주안역"))
                .downStation(createStation("동인천역"))
                .build();

        final Section addSection = Section.builder()
                .distance(10)
                .upStation(createStation("동인천역"))
                .downStation(createStation("서울역"))
                .build();


        sections = new Sections(initSection);
        sections.addSection(addSection);
    }


    @Test
    @DisplayName("이미 상행선과 하행선이 존재하는 경우 등록이 되지 않는다.")
    void existUpStationAndDownStation() {
        final Section newSection = Section.builder().upStation(createStation("주안역"))
                .downStation(createStation("서울역"))
                .distance(5).build();

        assertThatIllegalArgumentException().isThrownBy(() -> sections.addSection(newSection));
    }


    @Test
    @DisplayName("기존 역 사이의 길이보다 크거나 같으면 등록을 할 수 없음")
    void overSizeDistance() {
        //given
        final Section selSection = Section.builder()
                .distance(12)
                .upStation(createStation("인천역"))
                .downStation(createStation("서울역"))
                .build();

        //when & then
        assertThatIllegalStateException()
                .isThrownBy(() -> sections.addSection(selSection));
    }



}
