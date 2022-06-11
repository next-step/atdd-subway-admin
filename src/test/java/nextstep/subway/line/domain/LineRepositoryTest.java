package nextstep.subway.line.domain;

import nextstep.subway.station.domain.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;

class LineRepositoryTest {
    @Autowired
    StationRepository stations;
    @Autowired
    LineRepository lines;
}
