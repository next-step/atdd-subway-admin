package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철노선 관련 기능")
@Sql(value = {"classpath:db/truncate.sql"})
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class LineServiceTest {
    @Autowired
    LineService lines;

    @Autowired
    StationService stations;

    StationResponse gangnam;
    StationResponse gyodae;
    StationResponse sinchon;
    StationResponse sillim;

    @BeforeEach
    void beforeEach() {
        gangnam = stations.saveStation(new StationRequest("gangnam"));
        gyodae = stations.saveStation(new StationRequest("gyodae"));
        sinchon = stations.saveStation(new StationRequest("sinchon"));
        sillim = stations.saveStation(new StationRequest("sillim"));
    }

    @DisplayName("지하철 노선을 생성한다")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10);

        // when
        LineResponse response = lines.createLine(request);

        // then
        assertThat(response.getId()).isNotNull();
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void getLines() {
        // given
        lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10));
        lines.createLine(new LineRequest("2호선", "bg-blue-200", sinchon.getId(), sillim.getId(), 80));

        // when
        List<LineResponse> lines = this.lines.getLines();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10));

        // when
        LineResponse line = lines.getLineById(response.getId());

        // then
        assertThat(line.getId()).isNotNull();
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10));

        // when
        lines.deleteLineById(response.getId());

        // then
        assertThatThrownBy(() -> lines.getLineById(response.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("지하철 노선을 수정한다")
    @Test
    void updateLine() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10));

        // when
        lines.updateLineById(response.getId(), new Line("다른분당선", "bg-blue-100"));

        // then
        LineResponse line = lines.getLineById(response.getId());
        assertAll(
                () -> assertEquals("다른분당선", line.getName()),
                () -> assertEquals("bg-blue-100", line.getColor())
        );
    }
}