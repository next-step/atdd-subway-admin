package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.line.LineAlreadyExistException;
import nextstep.subway.exception.line.NotFoundLineException;
import nextstep.subway.exception.station.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

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
        validateBeforeLine(request);
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());
        Line line = request.toLine(upStation, downStation);

        return LineResponse.of(lineRepository.save(line));
    }

    private void validateBeforeLine(LineRequest request) {
        lineRepository.findByName(request.getName()).ifPresent(line -> {
            throw new LineAlreadyExistException();
        });
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return lineRepository.findById(id)
            .map(LineResponse::of)
            .orElseThrow(NotFoundLineException::new);
    }

    @Transactional(readOnly = true)
    public Station findStation(Long id) {
        return stationRepository
            .findById(id)
            .orElseThrow(NotFoundStationException::new);
    }

    public void updateLine(LineRequest lineRequest, Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());
        findedLine.update(lineRequest.toLine(upStation, downStation));
    }

    public void deleteLine(Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        lineRepository.delete(findedLine);
    }
}
