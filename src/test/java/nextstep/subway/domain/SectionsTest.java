package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private Station WANGSIPLI;
    private Station SANGWANGSIPLI;
    private Station SINDANG;
    private Station DDP;
    private Sections sections;

    @BeforeEach
    void setup() {
        WANGSIPLI = new Station("왕십리");
        SANGWANGSIPLI = new Station("상왕십리");
        SINDANG = new Station("신당");
        DDP = new Station("동대문역사문화공원");

        sections = new Sections(
            Arrays.asList(
                new Section(WANGSIPLI, SANGWANGSIPLI),
                new Section(SANGWANGSIPLI, SINDANG),
                new Section(SINDANG, DDP)
            )
        );
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        assertThat(sections.getStations()).contains(WANGSIPLI, SANGWANGSIPLI, SINDANG, DDP);
    }

}
