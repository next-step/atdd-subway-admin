package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DistanceTest {
    @Test
    @DisplayName("길이가 0보다 작을때 오류 확인")
    void 길이_양수_확인() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, ()->{
            Distance.from(0);
                });
    }
    
    @Test
    @DisplayName("길이가 더 긴 구간이 들어왔을때 오류 확인")
    void 길이_비교() {
        // given
        Distance distance = Distance.from(30);
        
        // when, then
        assertThrows(IllegalArgumentException.class, ()->{
            distance.minus(Distance.from(50));
                });
    }
    
    @DisplayName("길이가 짧아지는지 확인")
    @ParameterizedTest
    @CsvSource(value = { "30:20:10", "50:20:30" }, delimiter = ':')
    void 길이_줄어듦_확인(int oldDistance, int newDistance, int expected) {
        // given
        Distance distance = Distance.from(oldDistance);
        
        // when, then
        assertThat(distance.minus(Distance.from(newDistance))).isEqualTo(Distance.from(expected));
    }
    
    @DisplayName("길이가 길어지는지 확인")
    @ParameterizedTest
    @CsvSource(value = { "30:20:50", "50:20:70" }, delimiter = ':')
    void 길이_늘어남_확인(int oldDistance, int combineDistance, int expected) {
        // given
        Distance distance = Distance.from(oldDistance);
        
        // when, then
        assertThat(distance.plus(Distance.from(combineDistance))).isEqualTo(Distance.from(expected));
    }
}
