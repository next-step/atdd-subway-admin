package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    @DisplayName("상행 지하철역이 Null 일 경우 예외 테스트")
    @Test
    void createLineByNullUpStation() {
        Station downStation = Station.builder("새로운지하철역")
                .id(1L)
                .build();
        assertThatThrownBy(() -> Line.builder("신분당선", "bg-red-600", 10, null, downStation)
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
        assertThatThrownBy(() -> Line.builder("신분당선", "bg-red-600", 10, upStation, null)
                .build())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("하행역 정보가 없습니다.");
    }
}
