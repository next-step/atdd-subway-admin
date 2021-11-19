package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTest {
    public static final Station 강남역 = new Station("강남역");
    public static final Station 역삼역 = new Station("역삼역");
    public static final Station 양재역 = new Station("양재역");

    @Test
    @DisplayName("자하철 역 생성")
    void create(){
        assertThat(강남역).isEqualTo(new Station("강남역"));
    }
}
