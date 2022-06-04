package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        Section section = generateSection(lineRequest);
        persistLine.addSection(section);

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

    @Transactional
    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Section section = generateSection(sectionRequest);
        line.addSection(section);

        return section.toSectionResponse();
    }

    private Section generateSection(LineRequest lineRequest) {
        return generateSection(
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );
    }

    private Section generateSection(SectionRequest sectionRequest) {
        return generateSection(
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );
    }

    private Section generateSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findStationById(upStationId);
        Station downStation = stationService.findStationById(downStationId);
        return new Section(upStation, downStation, distance);
    }
}
