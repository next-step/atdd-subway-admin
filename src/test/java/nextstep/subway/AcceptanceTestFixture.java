package nextstep.subway;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptanceTestFixture {

    public static void 응답코드가_일치한다(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }
}
