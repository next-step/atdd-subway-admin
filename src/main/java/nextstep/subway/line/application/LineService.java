package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        line.addLineStation(request.getUpStationId(), request.getDownStationId(), request.getDistance());
        Line persistLine = lineRepository.save(line);

        List<Station> stations = getStations(line);
        return LineResponse.of(persistLine, stations);
    }

    private List<Station> getStations(Line line) {
        LineStations lineStations = line.getLineStations();
        List<Long> stationIds = lineStations.getStationIds();
        return stationRepository.findAllById(stationIds);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, getStations(line)))
                .collect(Collectors.toList());
    }

    public void updateLine(Long lineId, LineRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        line.update(request.toLine());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long lineId) {
        Line line = lineRepository.getOne(lineId);
        return LineResponse.of(line, getStations(line));
    }

    public void deleteLineById(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
