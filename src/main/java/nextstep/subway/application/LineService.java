package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.NotFoundLine;
import nextstep.subway.exception.NotFoundStation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveStation(LineRequest lineRequest) {
        Station upStation = stationService.findStation(lineRequest.getUpStationId());
        Station downStation = stationService.findStation(lineRequest.getDownStationId());
        Line line = lineRequest.toLine();
        line.addStations(upStation, downStation);
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLine::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLine::new);
        line.update(lineRequest);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLine::new);
        lineRepository.delete(line);
    }
}
