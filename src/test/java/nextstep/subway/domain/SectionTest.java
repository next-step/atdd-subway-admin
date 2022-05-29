package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    @DisplayName("상행 지하철역이 Null 일 경우 예외 테스트")
    @Test
    void createLineByNullUpStation() {
        Station downStation = Station.builder("새로운지하철역")
                .id(1L)
                .build();
        assertThatThrownBy(() -> Section.builder(null, downStation, Distance.valueOf(10))
                .build())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("상행역 정보가 없습니다.");
    }

    @DisplayName("하행 지하철역이 Null 일 경우 예외 테스트")
    @Test
    void createLineByNullDownStation() {
        Station upStation = Station.builder("지하철역")
                .id(1L)
                .build();
        assertThatThrownBy(() -> Section.builder(upStation, null, Distance.valueOf(10))
                .build())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("하행역 정보가 없습니다.");
    }
}
