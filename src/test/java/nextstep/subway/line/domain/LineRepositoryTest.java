package nextstep.subway.line.domain;

import nextstep.subway.BeforeJpaTestExecution;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LineRepositoryTest extends BeforeJpaTestExecution {

    @Autowired
    private LineRepository lines;

    private Line savedLine1;
    private Line savedLine2;

    @BeforeEach
    void beforeEach(){
        Line line1 = new Line("신분당선", "red");
        line1.addSection(new Section(강남역, 역삼역, line1, 7));
        Line line2 = new Line("2호선", "green");
        line2.addSection(new Section(강남역, 삼성역, line2, 10));

        savedLine1 = lines.save(line1);
        savedLine2 = lines.save(line2);
    }

    @Test
    @DisplayName("노선_생성")
    void createLine(){
        assertThat(savedLine1.getName()).isEqualTo("신분당선");
    }

    @Test
    @DisplayName("노선_구간포함_목록_조회_검증")
    void findAll(){
        List<Line> actual = lines.findAll();

        assertThat(actual).hasSize(2);
        assertThat(actual).contains(savedLine1, savedLine2);
        LineResponses responses = LineResponses.of(actual);
        for (LineResponse lineResponse : responses.toList()) {
            assertThat(lineResponse.getStations()).hasSize(2);
        }
    }

}