package nextstep.subway.line.application;

import java.util.List;

import nextstep.subway.line.application.exceptions.AlreadyExistsLineNameException;
import nextstep.subway.line.application.exceptions.NotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public Line saveLine(Line line, Long upStationId, Long downStationId, int distance) {
        checkAlreadyExists(line.getName());
        addSection(line, upStationId, downStationId, distance);
        return lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new NotFoundLineException(String.format("노선이 존재하지 않습니다.[%s]", lineId)));
    }

    public void updateLineById(Long lineId, Line updateLine) {
        Line line = findLineById(lineId);
        line.update(updateLine);
    }

    private void checkAlreadyExists(String name) {
        if (lineRepository.existsByName(name)) {
            throw new AlreadyExistsLineNameException(String.format("노선 이름이 이미 존재합니다.[%s]", name));
        }
    }

    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    private void addSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        line.addSection(upStation, downStation, distance);
    }
}
