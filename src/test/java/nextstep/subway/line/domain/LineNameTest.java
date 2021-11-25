package nextstep.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 이름 도메인 관련")
class LineNameTest {
    private LineName lineName;

    @BeforeEach
    void setUp() {
        lineName = new LineName("2호선");
    }

    @DisplayName("노선 이름을 저장한다.")
    @Test
    void createSections() {
        // then
        assertThat(lineName.getName()).isEqualTo("2호선");
    }

}