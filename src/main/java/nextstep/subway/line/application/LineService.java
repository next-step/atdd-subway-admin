package nextstep.subway.line.application;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line saveLine = lineRepository.save(request.toLine());
        saveLine.addSection(createSection(request));
        return LineResponse.of(saveLine);
    }

    private Section createSection(LineRequest request) {
        Station downStation = stationService.findByIdThrow(request.getDownStationId());
        Station upStation = stationService.findByIdThrow(request.getUpStationId());
        return new Section(downStation.getId(), upStation.getId(), new Distance(request.getDistance()));
    }

    public List<LineResponse> findAllLine() {
        List<Line> findAll = lineRepository.findAll();
        return LineResponse.ofList(findAll);
    }

    public LineResponse findLineById(final Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(final Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
