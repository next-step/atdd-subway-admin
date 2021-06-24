package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(
            new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance())
        );

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        return LineResponse.of(findLineByLineId(id));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineByLineId(id);
        line.update(lineRequest.toLine());

        Line persistLine = lineRepository.saveAndFlush(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        Line line = findLineByLineId(id);
        lineRepository.delete(line);
    }

    protected Line findLineByLineId(Long id) {
        return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
