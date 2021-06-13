package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DockingPositionTest {

    @DisplayName("구간의 앞단, 구간 중간의 앞단, 구간 중간의 뒷단은 인덱스 그대로이고, 구간의 뒷단은 인덱스가 1 증가한다.")
    @Test
    void 이어붙일_위치에_따른_인덱스값의_변화_테스트() {
        DockingPosition midfront = DockingPosition.midFront();
        DockingPosition rear = DockingPosition.rear();

        int testPositionIndex = 10;
        for (int i = 0; i < testPositionIndex; ++i) {
            midfront.nextIndex();
            rear.nextIndex();
        }
        assertThat(midfront.positionIndex()).isEqualTo(testPositionIndex);
        assertThat(rear.positionIndex()).isEqualTo(++testPositionIndex);
    }

    @Test
    void isDocked() {
        DockingPosition position1 = DockingPosition.none();
        assertThat(position1.isNotDockedYet()).isTrue();

        DockingPosition position2 = DockingPosition.midFront();
        assertThat(position2.isNotDockedYet()).isFalse();
    }

    @Test
    void create() {
        DockingPosition front = DockingPosition.front();

        int defaultIndex = 0;

        assertThat(front.index()).isEqualTo(defaultIndex);
    }

}
