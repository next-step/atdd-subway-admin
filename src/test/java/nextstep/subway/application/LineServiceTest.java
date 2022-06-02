package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철노선 관련 기능")
@Sql(value = {"classpath:db/data.sql"})
@ActiveProfiles(value = "acceptance")
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class LineServiceTest {
    @Autowired
    LineService service;

    @Autowired
    DatabaseCleaner cleaner;

    @AfterEach
    void afterEach() {
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
        assertThat(response.getId()).isNotNull();
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void getLines() {
        // given
        service.createLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));
        service.createLine(new LineRequest("2호선", "bg-blue-200", 3L, 4L, 80));

        // when
        List<LineResponse> lines = service.getLines();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // given
        LineResponse response = service.createLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

        // when
        LineResponse line = service.getLineById(response.getId());

        // then
        assertThat(line.getId()).isNotNull();
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineResponse response = service.createLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

        // when
        service.deleteLineById(response.getId());

        // then
        assertThatThrownBy(() -> service.getLineById(response.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("지하철 노선을 수정한다")
    @Test
    void updateLine() {
        // given
        LineResponse response = service.createLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

        // when
        service.updateLineById(response.getId(), new Line("다른분당선", "bg-blue-100"));

        // then
        LineResponse line = service.getLineById(response.getId());
        assertAll(
                () -> assertEquals("다른분당선", line.getName()),
                () -> assertEquals("bg-blue-100", line.getColor())
        );
    }
}