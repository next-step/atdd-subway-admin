package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Station upStationProxy = stationRepository.getById(request.getUpStationId());
        Station downStationProxy = stationRepository.getById(request.getDownStationId());
        Line line = request.toLine();
        line.setStations(upStationProxy,downStationProxy);
        Line save = lineRepository.save(line);
        return LineResponse.of(save);
    }
}
