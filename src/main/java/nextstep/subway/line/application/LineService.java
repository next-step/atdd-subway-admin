package nextstep.subway.line.application;

import nextstep.subway.exception.NotFountException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation =  stationRepository.findById(request.getUpStationId()).orElseThrow(NotFountException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(NotFountException::new);
        Line persistLine = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NotFountException::new);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(NotFountException::new);
        line.update(request.toLine());
    }

    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }
}
