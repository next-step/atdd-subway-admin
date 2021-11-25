package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionType;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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

    private List<Section> createSection(LineRequest request) {
        Station station = stationService.findByIdThrow(request.getUpStationId());
        Station downStation = stationService.findByIdThrow(request.getDownStationId());
        Section firstStation = new Section(station, downStation, SectionType.FIRST, new Distance(request.getDistance()));
        Section lastStation = new Section(station, downStation, SectionType.LAST, new Distance(request.getDistance()));
        return Arrays.asList(firstStation, lastStation);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();

        return LineResponse.ofList(lines);
    }

    @Transactional(readOnly = true)
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
