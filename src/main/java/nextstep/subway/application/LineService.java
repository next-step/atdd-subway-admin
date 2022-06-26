package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.NotFoundStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) throws NotFoundStationException {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(Line.of(lineRequest, upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse getLineById(Long id) {
        return LineResponse.of(lineRepository.getById(id));
    }

    @Transactional
    public Line findLineById(Long id) {
        return lineRepository.getById(id);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) throws NotFoundLineException {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundLineException(id));
        line.update(Line.of(lineUpdateRequest, line.getSections()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) throws NotFoundStationException {
        Line line = findLineById(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());

        Section newSection = Section.of(line, upStation, downStation, sectionRequest.getDistance());
        line.addSection(newSection);
        return LineResponse.of(line);
    }
}
