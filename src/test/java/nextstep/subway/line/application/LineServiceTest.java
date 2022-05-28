package nextstep.subway.line.application;

import nextstep.subway.line.apllication.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineServiceTest {
    @Test
    void create() {
        LineService lineService = new LineService();

        LineResponse lineResponse = lineService.create(LineRequest.of("2호선"));

        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }
}
