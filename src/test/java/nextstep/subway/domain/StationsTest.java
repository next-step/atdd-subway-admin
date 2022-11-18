package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationsTest {
    private Station WANGSIPLI;
    private Station SANGWANGSIPLI;
    private Station SINDANG;
    private Station DDP;

    @BeforeEach
    void setup() {
        WANGSIPLI = new Station(1L, "왕십리");
        SANGWANGSIPLI = new Station(2L, "상왕십리");
        SINDANG = new Station(3L, "신당");
        DDP = new Station(4L, "동대문역사문화공원");
    }

    @Test
    void 동등성() {
        assertThat(Stations.of(Arrays.asList(WANGSIPLI, SANGWANGSIPLI)))
            .isEqualTo(Stations.of(Arrays.asList(WANGSIPLI, SANGWANGSIPLI)));
        assertThat(Stations.of(Arrays.asList(WANGSIPLI, SANGWANGSIPLI)))
            .isNotEqualTo(Stations.of(Arrays.asList(WANGSIPLI, DDP)));
    }

    @DisplayName("중복제거 concat")
    @Test
    void concatDistinct() {
        assertThat(Stations.of(Arrays.asList(WANGSIPLI, SANGWANGSIPLI))
            .concatDistinct(Stations.of(Arrays.asList(WANGSIPLI, SINDANG, DDP)))
        ).isEqualTo(Stations.of(Arrays.asList(WANGSIPLI, SANGWANGSIPLI, SINDANG, DDP)));
    }
}
