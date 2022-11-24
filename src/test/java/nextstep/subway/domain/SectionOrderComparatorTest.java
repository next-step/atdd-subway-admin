package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 정렬 테스트")
public class SectionOrderComparatorTest {

    @DisplayName("오름차순 정렬 성공")
    @Test
    void sortByOrder_section_success() {
        //given:
        final Line line = new Line();
        final SectionOrderComparator sectionOrderComparator = new SectionOrderComparator();
        final List<Section> sectionList = new ArrayList<>(Arrays.asList(
                makeByOrder(3, line),
                makeByOrder(2, line),
                makeByOrder(4, line),
                makeByOrder(1, line)));
        //when:
        sectionList.sort(sectionOrderComparator);
        //then:
        assertThat(sectionList.stream()
                .map(Section::getOrderNumber)
                .collect(Collectors.toList())).containsSequence(1, 2, 3, 4);
    }

    private Section makeByOrder(int order, Line line) {
        return new Section(1L, 2L, new Distance(1), new Order(order), line);
    }
}

