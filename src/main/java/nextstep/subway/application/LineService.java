package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.error.LineNotFoundException;
import nextstep.subway.error.StationNotFoundException;
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
    public Line create(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());
        Line line = new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance());
        return lineRepository.save(line);
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new StationNotFoundException(stationId));
    }

    @Transactional(readOnly = true)
    public Lines getLines() {
        return new Lines(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Line get(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException(id));
    }

    @Transactional
    public void update(Long id, LineRequest request) {
        Line line = get(id);
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void remove(Long id) {
        Line line = get(id);
        lineRepository.delete(line);
    }
}
