package nextstep.subway.line.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("구간 일급컬렉션 관련 기능")
public class SectionsTest {
    @DisplayName("기존 구간들 사이에 상행역이 일치할 경우 새로운 구간을 추가한다.")
    @Test
    void add_matchUpStation() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("상행1"), new Station("하행2"), Distance.valueOf(4));

        Sections expectedSections = Sections.valueOf(Section.valueOf(new Station("상행1"), new Station("하행2"), Distance.valueOf(6)),
                                                    Section.valueOf(new Station("하행2"), new Station("하행1"), Distance.valueOf(4)));
        
        // when
        sections.add(newSection);

        // then
        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }

    @DisplayName("기존 구간들 사이에 하행역이 일치할 경우 새로운 구간을 추가한다.")
    @Test
    void add_matchDownStation() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("상행2"), new Station("하행1"), Distance.valueOf(4));

        Sections expectedSections = Sections.valueOf(Section.valueOf(new Station("상행1"), new Station("상행2"), Distance.valueOf(6)), 
                                                        Section.valueOf(new Station("상행2"), new Station("하행1"), Distance.valueOf(4)));
        
        // when
        sections.add(newSection);

        // then
        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }

    @DisplayName("기존 구간들 사이에 상행 종점을 추가한다.")
    @Test
    void add_upStaionTeminal() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("새로운 상행1"), new Station("상행1"), Distance.valueOf(5));

        Sections expectedSections = Sections.valueOf(newSection, section1);
        
        // when
        sections.add(newSection);

        // then
        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }

    @DisplayName("기존 구간들 사이에 하행 종점을 추가한다.")
    @Test
    void add_downStaionTeminal() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("하행1"), new Station("새로운 하행1"), Distance.valueOf(5));

        Sections expectedSections = Sections.valueOf(section1, newSection);
        
        // when
        sections.add(newSection);

        // then
        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }


    @DisplayName("구간이 등록되지 않았을때 새로운 구간을 등록한다.")
    @Test
    void add_empty_addStaion() {
        // given
        Sections sections = Sections.valueOf();

        Section newSection = Section.valueOf(new Station("하행1"), new Station("새로운 하행1"), Distance.valueOf(5));

        Sections expectedSections = Sections.valueOf(newSection);
        
        // when
        sections.add(newSection);

        // then
        Assertions.assertThat(sections).isEqualTo(expectedSections);
    }

    @DisplayName("추가되는 구간의 역중 하나가 기등록된 구간들의 역에 포함되지 않을때 에러를 발생")
    @Test
    void add_error_notContaionStation() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("상행2"), new Station("하행2"), Distance.valueOf(5));

        // when
        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> sections.add(newSection));
    }
    
    @DisplayName("추가되는 구간의 역중 하나가 기등록된 구간들의 역에 모두 포함될때 에러를 발생")
    @Test
    void add_error_allContaionStation() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(5));

        // when
        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> sections.add(newSection));
    }
    
    @DisplayName("추가되는 구간의 길이는 추가되는 역이 상행과 일치할 경우 그 역으로부터 하행역까지의 구간 길이 이상이면 에러가 발생")
    @Test
    void add_error_matchUpStation_overDistance() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("상행1"), new Station("하행2"), Distance.valueOf(15));

        // when
        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> sections.add(newSection));
    }

    @DisplayName("추가되는 구간의 길이는 추가되는 역이 하행과 일치할 경우 그 역으로부터 상행역까지의 구간 길이 이상이면 에러가 발생")
    @Test
    void add_error_matchDownStation_overDistance() {
        // given
        Section section1 = Section.valueOf(new Station("상행1"), new Station("하행1"), Distance.valueOf(10));
        Sections sections = Sections.valueOf(section1);

        Section newSection = Section.valueOf(new Station("상행2"), new Station("하행1"), Distance.valueOf(15));

        // when
        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> sections.add(newSection));
    }
}
