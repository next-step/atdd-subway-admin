package nextstep.subway.utils;

import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestConstructor(autowireMode = AutowireMode.ALL)
public class TearDownUtils {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public TearDownUtils(
        LineRepository lineRepository,
        StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void clearUpDatabase() {
        lineRepository.deleteAll();
        stationRepository.deleteAll();
    }
}
