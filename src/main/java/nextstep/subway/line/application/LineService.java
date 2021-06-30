package nextstep.subway.line.application;

import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Section section = new Section(upStation, downStation, request.getDistance());

        Line savedLine = lineRepository.save(request.toLine(section));

        return LineResponse.of(savedLine);
    }

    public LinesResponse findAll() {
        List<Line> lines = lineRepository.findAll();
        return LinesResponse.of(lines);
    }

    public LineResponse findById(Long id) {
        Line line = findByIdOrThrow(id);

        return LineResponse.of(line);
    }

    public void modify(Long id, LineRequest request) {
        Line line = findByIdOrThrow(id);
        line.update(request.toLine());
    }

    public Line findByIdOrThrow(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotExistLineException(id));
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSections(long lineId, SectionRequest request) {
        Line line = findByIdOrThrow(lineId);

        Station newUpStation = stationService.findById(request.getUpStationId());
        Station newDownStation = stationService.findById(request.getDownStationId());

        line.addSections(request.getDistance(), newUpStation, newDownStation);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = findByIdOrThrow(lineId);

        line.removeSectionByStation(stationService.findById(stationId));
    }
}
