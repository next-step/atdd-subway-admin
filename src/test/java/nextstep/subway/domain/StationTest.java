package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Station 도메인 객체에 대한 테스트")
public class StationTest {

    public static final Station 도봉산역 = new Station("도봉산역");
    public static final Station 수락산역 = new Station("수락산역");
    public static final Station 마들역 = new Station("마들역");
    public static final Station 노원역 = new Station("노원역");

    @DisplayName("객체를 생성한다.")
    @Test
    void createStation() {
        assertThat(수락산역.getName()).isEqualTo("수락산역");
    }
}
