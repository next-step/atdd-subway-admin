package nextstep.subway.line.domain;

import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    StationRepository stations;
    @Autowired
    LineRepository lines;

    @BeforeEach
    void setUp() {
        stations.save(StationTest.강남역);
        stations.save(StationTest.사당역);
    }
}