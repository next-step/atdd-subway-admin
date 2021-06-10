package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    private final StationService stationService;

    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = request.toLine();
        Section section = initializeSection(request.toSectionRequest());
        persistLine.addSection(section);
        return LineResponse.of(lineRepository.save(persistLine));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return LineResponse.ofList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(String id) {
        return LineResponse.of(findLineById(Long.valueOf(id)));

    }

    public void updateLine(String id, LineRequest request) {
        Line line = findLineById(Long.valueOf(id));
        line.update(request.toLine());
        lineRepository.save(line);
    }

    public void deleteLine(String id) {
        Line savedLine = findLineById(Long.valueOf(id));
        sectionRepository.deleteAllByLine(savedLine);
        lineRepository.delete(savedLine);
    }

    public LineResponse addSectionToLine(Long id, SectionRequest sectionRequest) {
        Section newSection = initializeSection(sectionRequest);
        Line toAddLine = findLineById(id);
        toAddLine.addSection(newSection);
        return LineResponse.of(lineRepository.save(toAddLine));
    }


    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Section initializeSection(SectionRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    public LineResponse deleteStationFromLine(Long lineId, Long stationId) {
        Line removeStationTarget = findLineById(lineId);
        removeStationTarget.removeStation(stationService.findStationById(stationId));
        return LineResponse.of(removeStationTarget);
    }
}
