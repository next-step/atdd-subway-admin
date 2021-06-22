package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;
import static nextstep.subway.line.domain.SectionTest.*;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.CannotRemoveException;
import nextstep.subway.line.exception.InvalidSectionException;
import nextstep.subway.station.exception.NoSuchStationException;

@DisplayName("Sections 단위 테스트")
class SectionsTest {

    Sections 강남_양재;
    Sections 강남_양재_광교중앙;
    Sections 강남_양재_광교중앙_광교;

    @BeforeEach
    void setUp() {
        강남_양재
            = new Sections(Stream.of(강남_양재_100).collect(toList()));
        강남_양재_광교중앙
            = new Sections(Stream.of(강남_양재_100, 양재_광교중앙_30).collect(toList()));
        강남_양재_광교중앙_광교
            = new Sections(Stream.of(강남_양재_100, 양재_광교중앙_30, 광교중앙_광교_30).collect(toList()));
    }

    @Test
    @DisplayName("구간들의 역을 상행역 순으로 가져온다")
    void getStationsInOrder() {
        assertThat(강남_양재_광교중앙_광교.getStationsInOrder())
            .containsExactly(강남역, 양재역, 광교중앙역, 광교역);
    }

    @Test
    @DisplayName("구간들이 구간을 포함하는지 확인한다")
    void contains() {
        assertThat(강남_양재.contains(강남_양재_100)).isTrue();
        assertThat(강남_양재.contains(광교중앙_광교_30)).isFalse();
    }

    @Test
    @DisplayName("구간들을 축약 가능한지 검증한다")
    void isReducible() {
        assertThat(강남_양재.isReducible()).isTrue();
        assertThat(강남_양재_광교중앙.isReducible()).isTrue();
        assertThat(강남_양재_광교중앙_광교.isReducible()).isFalse();
    }

    @Test
    @DisplayName("구간을 추가하고 결과를 확인한다")
    void addSection() {
        강남_양재.addSection(양재_광교중앙_30);
        assertThat(강남_양재.getStationsInOrder())
            .containsExactly(강남역, 양재역, 광교중앙역);
    }

    @Test
    @DisplayName("구간 추가 실패케이스를 검증한다")
    void addSection_Failed() {
        assertThatThrownBy(() -> 강남_양재.addSection(null))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> 강남_양재.addSection(광교중앙_광교_30))
            .isInstanceOf(InvalidSectionException.class);
        assertThatThrownBy(() -> 강남_양재.addSection(강남_양재_100))
            .isInstanceOf(InvalidSectionException.class);
    }

    @Test
    @DisplayName("구간과 구간 사이의 역을 제거한다")
    void removeStation_removeMiddle() {
        강남_양재_광교중앙.removeStation(양재역);
        assertThat(강남_양재_광교중앙.getStationsInOrder())
            .containsExactly(강남역, 광교중앙역);
    }

    @Test
    @DisplayName("구간들의 양 끝 역을 삭제한다")
    void removeStation_removeSide() {
        강남_양재_광교중앙_광교.removeStation(광교역);
        assertThat(강남_양재_광교중앙_광교.getStationsInOrder())
            .containsExactly(강남역, 양재역, 광교중앙역);

        강남_양재_광교중앙.removeStation(강남역);
        assertThat(강남_양재_광교중앙.getStationsInOrder())
            .containsExactly(양재역, 광교중앙역);
    }

    @Test
    @DisplayName("구간 삭제 실페케이스를 검증한다")
    void removeStation_Failed() {
        assertThatThrownBy(() -> 강남_양재.removeStation(강남역))
            .isInstanceOf(CannotRemoveException.class);
        assertThatThrownBy(() -> 강남_양재.removeStation(광교역))
            .isInstanceOf(NoSuchStationException.class);
    }
}
