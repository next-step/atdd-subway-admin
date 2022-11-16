package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

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
    @DisplayName("역 조회하기 성공")
    void getStations() {
        assertThat(section1.getStations()).containsExactly(종각역, 용산역);
    }

    @Test
    @DisplayName("역 사이에 구간 추가 할 때, 기존 구간의 거리보다 크거나 같을 경우 예외처리")
    void validateDistance() {
        section2.changeDistance(15);

        assertThatThrownBy(() -> section1.validateDistance(section2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("최초 구간 상향, 하향 종점역으로 등록 성공")
    void registerEndPoint() {
        section1.registerEndPoint();

        assertThat(section1.isAscentEndpoint()).isTrue();
        assertThat(section1.isDeAscentEndpoint()).isTrue();
    }

    @Test
    @DisplayName("구간에 포함되는 역이 존재하는 지 확인 성공")
    void isInclude() {
        assertAll(
                () -> assertThat(section1.isInclude(1L)).isTrue(),
                () -> assertThat(section1.isInclude(10L)).isFalse()
        );
    }

    @Test
    @DisplayName("구간을 추가 했을 때 구간 재배치에 성공")
    void reArrangeWith() {
        int distance = 7;
        Section section = new Section(종각역, new Station("안양역"), distance);
        int expectedDistance = section1.getDistance() - distance;

        section1.reArrangeWith(section);

        assertThat(section1.getDistance()).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("2개의 구간의 하향역이 같은지 확인 성공")
    void isSameDownStation() {
        assertThat(section1.isSameDownStation(new Section(new Station("구로역"), 용산역, 10))).isTrue();
    }

    @Test
    @DisplayName("2개의 구간의 상향역이 같은지 확인 성공")
    void isSameUpStation() {
        assertThat(section1.isSameUpStation(new Section(종각역, new Station("용산역"), 10))).isTrue();
    }
}
