package nextstep.subway.line.application;

import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Section;
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
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest request) {
        Section upSection = createSectionByFoundStation(request.getUpStationId(), SectionStatus.UP);
        Section downSection = createSectionByFoundStation(request.getUpStationId(), SectionStatus.DOWN);

        Line persistLine = lineRepository.save(request.toLine());

        return LineResponse.of(persistLine.addUpSectionAndDownSection(upSection, downSection));
    }

    private Section createSectionByFoundStation(long stationId, SectionStatus status) {
        Station upStation = stationService.findById(stationId);
        return new Section(status, upStation);
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
