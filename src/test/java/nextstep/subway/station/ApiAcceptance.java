package nextstep.subway.station;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ApiAcceptance {
    public static void API응답_검증(int givenStatusCode, int whenStatusCode) {
        assertThat(givenStatusCode).isEqualTo(whenStatusCode);
    }
}
