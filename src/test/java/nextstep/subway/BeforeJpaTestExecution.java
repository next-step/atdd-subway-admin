package nextstep.subway;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BeforeJpaTestExecution {
    @Autowired
    private StationRepository stations;

    public Station 강남역;
    public Station 역삼역;
    public Station 삼성역;

    @BeforeEach
    public void preExecution() {
        강남역 = stations.save(new Station("강남역"));
        역삼역 = stations.save(new Station("역삼역"));
        삼성역 = stations.save(new Station("삼성역"));
    }
}
