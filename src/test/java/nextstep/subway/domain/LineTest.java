package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import javax.persistence.EntityManager;
import nextstep.subway.domain.collection.Stations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineTest {

    private static final Line SIN_BOUN_DANG_LINE = new Line("신분당선","bg-red-600");

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private EntityManager entityManager;

    @DisplayName("노선 정보를 업데이트 한다.")
    @Test
    void update(){
        //given
        Line line = lineRepository.save(SIN_BOUN_DANG_LINE);

        //when
        line.update("분당선","bg-yellow-600");
        entityManager.flush();
        entityManager.clear();

        //then
        Line actual = lineRepository.findById(line.getId()).get();
        assertThat(actual.getName()).isEqualTo("분당선");
        assertThat(actual.getColor()).isEqualTo("bg-yellow-600");
    }

    @DisplayName("노선 정보를 업데이트 한다.")
    @Test
    void delete(){
        //given
        Line line = lineRepository.save(SIN_BOUN_DANG_LINE);

        //when
        line.delete();
        entityManager.flush();
        entityManager.clear();

        //then
        Line actual = lineRepository.findById(line.getId()).get();
        assertThat(actual.isDeleted()).isTrue();
    }

    @DisplayName("노선에 지하철역을 추가한다.")
    @Test
    void addStation(){
        //given
        Station pangyo = new Station("판교");
        Station jeongja = new Station("정자");
        Line line = lineRepository.save(SIN_BOUN_DANG_LINE);

        //when
        line.addStation(pangyo);
        line.addStation(jeongja);
        entityManager.flush();
        entityManager.clear();

        //then
        Line actual = lineRepository.findById(line.getId()).get();
        assertThat(actual.getStations()).isEqualTo(new Stations(Arrays.asList(pangyo,jeongja)));
    }
}
