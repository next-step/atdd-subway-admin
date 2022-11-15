package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("노선 색 테스트")
class ColorTest {

    @DisplayName("생성 성공")
    @Test
    void create_color_success() {
        //given:
        String colorName = "bg-red-600";
        //when:
        Color color = new Color(colorName);
        //then:
        assertThat(color.getColor()).isEqualTo(colorName);
    }

    @DisplayName("생성 실패 - 올바르지 않은 색 정보")
    @Test
    void create_color_IllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Color("color"));
    }
}
