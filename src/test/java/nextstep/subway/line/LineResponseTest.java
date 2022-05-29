package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineResponseTest {
    private static final Line line = new Line(1L,"신분당선", "bg-red-600", new Station(1L,"지하철역"), new Station(2L,"새로운지하철역"), 10L);
    private List<Station> stations;
    private LineResponse lineResponse;

    @BeforeEach
    void setUp() {
        stations = Arrays.asList(new Station(1L,"지하철역"), new Station(2L, "새로운지하철역"));
        lineResponse = new LineResponse(1L, "신분당선", "bg-red-600", stations, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createTest() {
        assertThat(lineResponse).isEqualTo(new LineResponse(1L, "신분당선", "bg-red-600", stations, LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    void ofTest() {
        assertThat(LineResponse.of(line)).isEqualTo(lineResponse);
    }
}
