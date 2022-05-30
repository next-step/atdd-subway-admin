package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());

        Section section = generateSection(lineRequest);
        persistLine.addSection(section);
        sectionRepository.save(section);

        return persistLine.toLineResponse();
    }

    public List<LineResponse> findAllLines() {
        Lines lines = Lines.from(lineRepository.findAll());
        return lines.toLineResponses();
    }

    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return line.toLineResponse();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Section generateSection(LineRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());
        return new Section(upStation, downStation, lineRequest.getDistance());
    }
}
