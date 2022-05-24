package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.NotFoundLineException;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.NewLineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.UpdateLineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository repository;
    private final StationService stationService;

    public LineService(LineRepository repository, StationService stationService) {
        this.repository = repository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(NewLineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line savedLine = repository.save(new Line(request.getName(), request.getColor(), upStation, downStation,
                                                  request.getDistance()));
        return new LineResponse(savedLine);
    }

    public List<LineResponse> findAllLines() {
        return repository.findAll()
                         .stream()
                         .map(LineResponse::new)
                         .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return repository.findById(id)
                         .map(LineResponse::new)
                         .orElseThrow(() -> new NotFoundLineException(id));
    }

    public Line findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id));
    }

    @Transactional
    public void updateLine(UpdateLineRequest request) {
        Line line = repository.findById(request.getId())
                              .orElseThrow(() -> new NotFoundLineException(request.getId()));
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        repository.deleteById(id);
    }
}
