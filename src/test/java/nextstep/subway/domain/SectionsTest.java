package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private Station 강남역 = new Station("강남역");;
    private Station 정자역 = new Station("정자역");
    private Station 미금역 = new Station("미금역");;
    private Station 광교역 = new Station("광교역");;
    private Sections 구간_목록 = new Sections();

    
    @DisplayName("노선에 새로운 구간을 추가할때 두개 구간이 전부 노선에 없으면 IllegalArgumentException이 발생한다.")
    @Test
    void add_noMatching_section_exception() {
        구간_목록.add(new Section(강남역, 광교역, 10));
        // given
        assertFalse(구간_목록에_역이_존재한다(구간_목록, 정자역));
        assertFalse(구간_목록에_역이_존재한다(구간_목록, 미금역));

        Section 새구간 = new Section(정자역, 미금역, 10);
        // when
        assertThatThrownBy(() ->구간_목록.add(새구간))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선에 새로운 구간을 추가할때 두개 구간이 전부 노선에 있으면 IllegalArgumentException이 발생한다.")
    @Test
    void add_duplicated_section_exception() {
        Section 구간 = new Section(강남역, 광교역, 10);
        구간_목록.add(구간);
        // when
        assertThatThrownBy(() ->구간_목록.add(구간))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("새로운 상행종점 또는 하행종점을 추가할 수 있다.")
    @Test
    void addFirstOrLastSection_test() {
        // given
        Section 구간 = new Section(정자역, 미금역, 10);
        구간_목록.add(구간);

        구간_목록.add(new Section(강남역, 정자역, 10));
        구간_목록.add(new Section(미금역, 광교역, 10));

        // then
        assertTrue(구간_목록에_역이_존재한다(구간_목록, 강남역, 정자역, 미금역, 광교역));
    }


    @DisplayName("이미 존재하는 구간의 중간에 새로운 구간을 추가할 수 있다.")
    @Test
    void addSectionInMiddle_test() {
        Section 구간 = new Section(강남역, 미금역, 10);
        구간_목록.add(구간);

        구간_목록.add(new Section(정자역, 미금역, 5));

        // then
        assertTrue(구간_목록에_역이_존재한다(구간_목록, 정자역));
    }

    @DisplayName("노선의 상행종점부터 하행 종점까지 순차적으로 정렬하여 조회 가능하다.")
    @Test
    void getOrderedStations_test() {
        구간_목록.add(new Section(정자역, 미금역, 10));
        구간_목록.add(new Section(강남역, 정자역, 10));
        구간_목록.add(new Section(미금역, 광교역, 10));

        List<Station> 정렬된_역들 = 구간_목록.getOrderedStations();

        assertAll(
                ()->assertEquals(강남역, 정렬된_역들.get(0)),
                ()->assertEquals(정자역, 정렬된_역들.get(1)),
                ()->assertEquals(미금역, 정렬된_역들.get(2)),
                ()->assertEquals(광교역, 정렬된_역들.get(3))

        );
    }

    private boolean 구간_목록에_역이_존재한다(Sections 구간_목록, Station...역){
        return 구간_목록.getAll()
                .stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(역));
    }



}