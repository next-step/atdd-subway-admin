package nextstep.subway.application;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철노선 서비스 관련 기능")
@ActiveProfiles(value = "acceptance")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class LineServiceTest {
    @Autowired
    LineService service;

    @Autowired
    DatabaseCleaner cleaner;

    @BeforeEach
    public void setUp() {
        cleaner.execute();
    }

    @DisplayName("지하철 노선을 생성한다")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        LineResponse response = service.createLine(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void getLines() {
        // given
        List<LineRequest> requests = Arrays.asList(
                new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10),
                new LineRequest("2호선", "bg-blue-200", 3L, 4L, 80)
        );

        for (LineRequest request : requests) {
            service.createLine(request);
        }

        // when
        List<LineResponse> lines = service.getLines();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineResponse response = service.createLine(request);

        // when
        LineResponse line = service.getLine(response.getId());

        // then
        assertThat(line).isNotNull();
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineResponse response = service.createLine(request);

        // when
        service.deleteLineById(response.getId());

        // then
        assertThatThrownBy(() -> service.getLine(response.getId())).isInstanceOf(NoSuchElementException.class);
    }
}