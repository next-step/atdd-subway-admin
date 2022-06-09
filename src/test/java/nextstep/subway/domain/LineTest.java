package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LineTest {
    private final static Station station1 = new Station("지하철역1");
    private final static Station station2 = new Station("지하철역2");
    private final static Station station3 = new Station("지하철역3");
    private final static Station station4 = new Station("지하철역4");

    @DisplayName("지하철 업데이트 시 업데이트 내용으로 변경되어야 한다")
    @Test
    void lineUpdateTest() {
        // given
        Line line = new Line("지하철", "bg-green-600", 10L, station1, station2);
        LineRequest.Modification modification = mock(LineRequest.Modification.class);
        given(modification.getName()).willReturn("새로운 지하철");
        given(modification.getColor()).willReturn("bg-blue-600");

        // when
        line.modify(modification);

        // then
        assertThat(line.getName()).isEqualTo("새로운 지하철");
        assertThat(line.getColor()).isEqualTo("bg-blue-600");
    }

    @DisplayName("지하철 노선에 구간을 추가하면 해당 위치에 정상 추가되어야 한다")
    @Test
    void addLineStationTest() {
        // given
        Line lineWillAddFirst = new Line("지하철1", "bg-green-600", 10L, station1, station2);
        Line lineWillAddMiddle = new Line("지하철2", "bg-blue-600", 10L, station1, station2);
        Line lineWillAddLast = new Line("지하철3", "bg-purple-600", 10L, station1, station2);

        // when
        lineWillAddFirst.addSection(station3, station1, 10L);
        lineWillAddMiddle.addSection(station1, station3, 5L);
        lineWillAddLast.addSection(station2, station3, 10L);

        // then
        assertThat(lineWillAddFirst.getStationOrderedUpToDown()).containsExactly(station3, station1, station2);
        assertThat(lineWillAddMiddle.getStationOrderedUpToDown()).containsExactly(station1, station3, station2);
        assertThat(lineWillAddLast.getStationOrderedUpToDown()).containsExactly(station1, station2, station3);
    }

    @DisplayName("지하철 노선에 중간 노선을 추가할 때 기존의 노선 길이보다 크거나 같은 노선을 추가하면 예외가 발생해야 한다")
    @Test
    void addMiddleByLongerDistanceLineStationTest() {
        // given
        Line line = new Line("지하철", "bg-green-600", 10L, station1, station2);

        // then
        assertThatThrownBy(() -> line.addSection(station1, station3, 10L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> line.addSection(station1, station3, 11L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선에 존재하지 않는 지하철을 추가하면 예외가 발생해야 한다")
    @Test
    void addNotContainStationTest() {
        // given
        Line line = new Line("지하철", "bg-green-600", 10L, station1, station2);

        // then
        assertThatThrownBy(() -> line.addSection(station3, station4, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("2개 이상 구간을 가진 지하철의 특정 노선을 제거하면 해당 구간이 제거되고 기존 순서는 유지되어야 한다")
    @Test
    void removeLineStationTest() {
        // given
        Line lineWillRemoveFirst = new Line("지하철1", "bg-green-600", 10L, station1, station2);
        lineWillRemoveFirst.addSection(station2, station3, 10L);
        lineWillRemoveFirst.addSection(station3, station4, 10L);
        Line lineWillRemoveMiddle = new Line("지하철2", "bg-blue-600", 10L, station1, station2);
        lineWillRemoveMiddle.addSection(station2, station3, 10L);
        lineWillRemoveMiddle.addSection(station3, station4, 10L);
        Line lineWillRemoveLast = new Line("지하철3", "bg-purple-600", 10L, station1, station2);
        lineWillRemoveLast.addSection(station2, station3, 10L);
        lineWillRemoveLast.addSection(station3, station4, 10L);

        // when
        lineWillRemoveFirst.deleteSection(station1);
        lineWillRemoveMiddle.deleteSection(station2);
        lineWillRemoveLast.deleteSection(station4);

        // then
        assertThat(lineWillRemoveFirst.getStationOrderedUpToDown()).containsExactly(station2, station3, station4);
        assertThat(lineWillRemoveMiddle.getStationOrderedUpToDown()).containsExactly(station1, station3, station4);
        assertThat(lineWillRemoveLast.getStationOrderedUpToDown()).containsExactly(station1, station2, station3);
    }

    @DisplayName("1개 이하의 구간을 가진 지하철 노선의 특정 구간을 삭제하면 예외가 발생해야 한다")
    @Test
    void removeLineStationWithLessThanMinimumLineStationCountTest() {
        // given
        Line line = new Line("지하철1", "bg-green-600", 10L, station1, station2);

        // then
        assertThatThrownBy(() -> line.deleteSection(station1)).isInstanceOf(IllegalArgumentException.class);
    }
}
