package nextstep.subway.line.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineNotFoundException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationNotFoundException;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) throws
            LineNameDuplicatedException,
            StationNotFoundException {
        checkNameDuplication(request.getName());
        int distance = request.getDistance();
        Station upStation = stationService.getById(request.getUpStationId());
        Station downStation = stationService.getById(request.getDownStationId());
        Line line = request.toLine(upStation, downStation, distance);
        return LineResponse.of(lineRepository.save(line));
    }

    private void checkNameDuplication(String name) throws LineNameDuplicatedException {
        if (lineRepository.findByName(name).isPresent()) {
            throw new LineNameDuplicatedException(name);
        }
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(toList());
    }

    public LineResponse findById(Long id) throws LineNotFoundException {
        return LineResponse.of(lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new)
        );
    }

    public LineResponse updateById(Long id, LineRequest request) throws LineNotFoundException {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        line.update(request.getName(), request.getColor());
        return LineResponse.of(line);
    }

    public void deleteById(Long id) throws LineNotFoundException {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        lineRepository.delete(line);
    }
}
