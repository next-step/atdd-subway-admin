package nextstep.subway.application;

import nextstep.subway.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
        gangnam = stations.saveStation(new StationRequest("강남역"));
        gyodae = stations.saveStation(new StationRequest("교대역"));
        sinchon = stations.saveStation(new StationRequest("신촌역"));
        sillim = stations.saveStation(new StationRequest("신림역"));
    }

    @DisplayName("지하철 노선을 생성한다")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L);

        // when
        LineResponse response = lines.createLine(request);

        // then
        assertThat(response.getId()).isNotNull();
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void getLines() {
        // given
        lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));
        lines.createLine(new LineRequest("2호선", "bg-blue-200", sinchon.getId(), sillim.getId(), 80L));

        // when
        List<LineResponse> lines = this.lines.getLines();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));

        // when
        LineResponse line = lines.getLine(response.getId());

        // then
        assertThat(line.getId()).isNotNull();
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));

        // when
        lines.deleteLine(response.getId());

        // then
        assertThatThrownBy(() -> lines.getLine(response.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("지하철 노선을 수정한다")
    @Test
    void updateLine() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));

        // when
        lines.updateLine(response.getId(), new LineRequest("다른분당선", "bg-blue-100", gangnam.getId(), gyodae.getId(), 10L));

        // then
        LineResponse line = lines.getLine(response.getId());
        assertAll(
                () -> assertEquals("다른분당선", line.getName()),
                () -> assertEquals("bg-blue-100", line.getColor())
        );
    }

    @DisplayName("노선 중간에 구간을 등록한다.")
    @Test
    void addSectionBetweenStations() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));

        // when
        lines.addSection(response.getId(), new SectionRequest(gangnam.getId(), sinchon.getId(), 10L));

        // then
        List<String> names = lines.getLine(response.getId())
                                  .getStations()
                                  .stream()
                                  .map(LineResponse.StationDto::getName)
                                  .collect(Collectors.toList());
        assertThat(names).contains("강남역", "신촌역", "교대역");
    }

    @DisplayName("노선 시작 구간을 추가한다.")
    @Test
    void addSectionAtBegin() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));

        // when
        lines.addSection(response.getId(), new SectionRequest(sinchon.getId(), gangnam.getId(), 10L));

        // then
        List<String> names = lines.getLine(response.getId())
                                  .getStations()
                                  .stream()
                                  .map(LineResponse.StationDto::getName)
                                  .collect(Collectors.toList());
        assertThat(names).contains("신촌역", "강남역", "교대역");
    }

    @DisplayName("노선 마지막에 구간을 등록한다.")
    @Test
    void addSectionAtEnd() {
        // given
        LineResponse response = lines.createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));

        // when
        lines.addSection(response.getId(), new SectionRequest(gyodae.getId(), sinchon.getId(), 10L));

        // then
        List<String> names = lines.getLine(response.getId())
                                  .getStations()
                                  .stream()
                                  .map(LineResponse.StationDto::getName)
                                  .collect(Collectors.toList());
        assertThat(names).contains("강남역", "교대역", "신촌역");
    }
}