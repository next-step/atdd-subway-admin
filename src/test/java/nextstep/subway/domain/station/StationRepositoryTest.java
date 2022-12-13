package nextstep.subway.domain.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    public Station 지하철역_생성됨(String name) {
        return stationRepository.save(new Station(name));
    }
}