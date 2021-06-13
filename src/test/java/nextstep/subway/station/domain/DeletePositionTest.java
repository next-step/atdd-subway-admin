package nextstep.subway.station.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePositionTest {

    @Test
    void create() {
        DeletePosition deletePosition = DeletePosition.None();

        assertThat(deletePosition.index()).isEqualTo(0);
    }

    @Test
    void 순회문에서의_인덱스_값_확인() {
        DeletePosition deletePosition = DeletePosition.None();

        for (int i = 0; i < 10; ++i) {
            deletePosition.nextIndex();
        }
        assertThat(deletePosition.index()).isEqualTo(10);
    }

    @Test
    void DeletePosition_의_상태변화와_검증() {
        DeletePosition deletePosition = DeletePosition.None();

        assertThat(deletePosition.typeUpInHead().isUpInHead()).isTrue();
        assertThat(deletePosition.typeUpInTail().isUpInTail()).isTrue();
        assertThat(deletePosition.typeUpInMiddles().isUpInMiddles()).isTrue();
        assertThat(deletePosition.typeDownInTail().isDownInTail()).isTrue();
    }

}
