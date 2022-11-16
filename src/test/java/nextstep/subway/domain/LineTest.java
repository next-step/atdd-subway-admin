package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    Station 종각역;
    Station 용산역;
    Station 노량진역;
    Section section1;
    Section section2;

    @BeforeEach
    void setup() {
        종각역 = new Station("종각역");
        용산역 = new Station("용산역");
        노량진역 = new Station("노량진역");
        setId(종각역, 1L);
        setId(용산역, 2L);
        setId(노량진역, 3L);
        section1 = new Section(종각역, 용산역, 10);
        section2 = new Section(용산역, 노량진역, 7);
    }

    private void setId(final Station station, final long value) {
        Field id = ReflectionUtils.findField(Station.class, "id");
        assert id != null;
        id.setAccessible(true);
        ReflectionUtils.setField(id, station, value);
    }

    @Test
    @DisplayName("노선 생성 성공")
    void of() {
        Line actual = Line.of("1호선", "blue", section1);

        assertThat(actual.getSections()).containsExactly(section1);
    }

    @Test
    @DisplayName("구간 추가 성공")
    void addSection() {
        Line line = Line.of("1호선", "blue", section1);

        line.addSection(section2);

        assertThat(line.getSections()).containsExactly(section1, section2);
    }

    @Test
    @DisplayName("구간 삭제 성공")
    void deleteSectionByStationId() {
        Line line = Line.of("1호선", "blue", section1);
        line.addSection(section2);

        line.deleteSectionByStationId(종각역.getId());

        assertThat(line.getSections()).hasSize(1);
    }
}
