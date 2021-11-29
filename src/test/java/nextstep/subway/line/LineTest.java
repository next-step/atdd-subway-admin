package nextstep.subway.line;

import nextstep.subway.Exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext
@DataJpaTest
public class LineTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private Station 강남역;
    private Station 광교역;
    private Station 왕십리역;
    private Station 수원역;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        광교역 = stationRepository.save(new Station("광교역"));
        왕십리역 = stationRepository.save(new Station("왕십리역"));
        수원역 = stationRepository.save(new Station("수원역"));
    }

    @DisplayName("노선 저장")
    @Test
    void 저장() {
        //when
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 광교역, 10));

        //then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("신분당선");
    }

    @DisplayName("노선 조회")
    @Test
    void 조회() {
        //given
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 광교역, 10));

        //when
        Line result = lineRepository.findById(line.getId()).orElseThrow(() -> new NotFoundException("데이터 없음" + line.getId()));

        //then
        assertThat(result).isEqualTo(line);
    }

    @DisplayName("노선 수정")
    @Test
    void 수정() {
        //given
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 광교역, 10));
        Line expected = lineRepository.save(new Line("분당선", "bg-yellow-200", 왕십리역, 수원역, 20));

        //when
        line.update(expected);
        Line result = lineRepository.findById(line.getId()).orElseThrow(() -> new NotFoundException("데이터 없음" + line.getId()));

        //then
        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getColor()).isEqualTo(expected.getColor());
    }


    @DisplayName("노선 삭제")
    @Test
    void 삭제() {
        //given
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 광교역, 10));

        //when
        lineRepository.delete(line);

        //then
        assertThatThrownBy(() -> {
            lineRepository.findById(line.getId()).orElseThrow(() -> new NotFoundException("데이터 없음" + line.getId()));
        }).isInstanceOf(NotFoundException.class).hasMessage("데이터 없음" + line.getId());
    }
}
