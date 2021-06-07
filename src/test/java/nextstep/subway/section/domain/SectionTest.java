package nextstep.subway.section.domain;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class SectionTest {
    public static final Section 강남_판교_구간 = new Section(1L, 강남역, 판교역, 5);
    public static final Section 판교_수지_구간 = new Section(2L, 판교역, 수지역, 5);
    public static final Section 수지_광교_구간 = new Section(3L, 수지역, 광교역, 5);


    @Test
    @DisplayName("생성 테스트")
    void create() {
        // given & when & then
        assertThat(강남_판교_구간.getUpStation()).isEqualTo(강남역);
        assertThat(강남_판교_구간.getDownStation()).isEqualTo(판교역);
        assertThat(강남_판교_구간.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("하나의 역에 동일한 상행/하행선은 등록 불가능 테스트")
    void upDownStationEqualsRegisterException() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Section(강남역, 강남역, 5));
    }

    @Test
    @DisplayName("유효하지 않은 거리(0보다 작은) 등록 테스트")
    void distanceRegisterException() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Section(강남역, 판교역, -1));
    }

    @Test
    @DisplayName("구간 내 존재하는 역 반환 테스트")
    void toStation() {
        // given & when
        List<Station> stations = 강남_판교_구간.toStations();
        // then
        assertThat(stations.get(0)).isEqualTo(강남역);
        assertThat(stations.get(1)).isEqualTo(판교역);
   }

    @Test
    @DisplayName("구간 내 거리 보다 같거나 큰 경우, 수정 불가 테스트")
    void updateDistanceValidate() {
        // given
        Section 강남_양재_구간 = new Section(5L, 강남역, 양재역, 5);
        Section 강남_수지_구간 = new Section(5L, 강남역, 양재역, 10);
        // when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> 강남_판교_구간.updateSection(강남_양재_구간));
        Assertions.assertThrows(IllegalArgumentException.class, () -> 강남_판교_구간.updateSection(강남_수지_구간));
    }

    @Test
    @DisplayName("상행선이 같은 구간 추가 시, 수정 테스트")
    void updateSameUpStation() {
        // given
        Section 강남_판교_구간 = new Section(1L, 강남역, 판교역, 5);
        Section 강남_양재_구간 = new Section(5L, 강남역, 양재역, 2);
        // when
        강남_판교_구간.updateSection(강남_양재_구간);
        // then
        assertThat(강남_판교_구간.getUpStation()).isEqualTo(양재역);
        assertThat(강남_판교_구간.getDownStation()).isEqualTo(판교역);
        assertThat(강남_판교_구간.getDistance()).isEqualTo(3);
    }

    @Test
    @DisplayName("하행선이 같은 구간 추가 시, 수정 테스트")
    void updateSameDownStation() {
        // given
        Section 강남_판교_구간 = new Section(1L, 강남역, 판교역, 5);
        Section 양재_판교_구간 = new Section(5L, 양재역, 판교역, 1);
        // when
        강남_판교_구간.updateSection(양재_판교_구간);
        // then
        assertThat(강남_판교_구간.getUpStation()).isEqualTo(강남역);
        assertThat(강남_판교_구간.getDownStation()).isEqualTo(양재역);
        assertThat(강남_판교_구간.getDistance()).isEqualTo(4);
    }

    @Test
    @DisplayName("비교 구간의 상행선 또는 하행선 포함 확인 테스트")
    void isContainSection() {
        // given
        Section 강남_광교_구간 = new Section(5L, 강남역, 광교역, 5);
        // when & then
        assertThat(강남_광교_구간.isContainSection(강남_판교_구간)).isTrue();
        assertThat(강남_광교_구간.isContainSection(수지_광교_구간)).isTrue();
        assertThat(강남_판교_구간.isContainSection(판교_수지_구간)).isFalse();
    }
}
