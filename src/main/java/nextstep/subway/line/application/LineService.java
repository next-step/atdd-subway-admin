package nextstep.subway.line.application;

import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.SectionStatus;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        Section upSection = saveSectionByFoundStation(request.getUpStationId(), persistLine, SectionStatus.UP);
        Section downSection = saveSectionByFoundStation(request.getUpStationId(), persistLine, SectionStatus.DOWN);

        return LineResponse.of(persistLine.addUpSectionAndDownSection(upSection, downSection));
    }

    private Section saveSectionByFoundStation(long stationId, Line persistLine, SectionStatus status) {
        Station upStation = stationService.findById(stationId);
        return sectionRepository.save(new Section(status, upStation, persistLine));
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
}
