package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidEntityRequiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("노선 이름 도메인 관련")
class LineNameTest {
    private LineName lineName;

    @BeforeEach
    void setUp() {
        lineName = new LineName("2호선");
    }

    @DisplayName("노선 이름을 저장한다.")
    @Test
    void createLineName() {
        // then
        assertThat(lineName.getName()).isEqualTo("2호선");
    }

    @DisplayName("노선 이름은 빈 이름을 가질 수 없다.")
    @Test
    void emptyLineNameException() {
        assertThatThrownBy(() -> {
            final LineName emptyLineName = new LineName("");

        }).isInstanceOf(InvalidEntityRequiredException.class)
                .hasMessageContaining("필수로 필요한 Entity member가 들어오지 않았습니다.");
    }

}