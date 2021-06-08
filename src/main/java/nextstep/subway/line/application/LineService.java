package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        sectionService.saveSection(persistLine, upStation, downStation, request.getDistance());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void editLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    private Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line line = findById(id);
        sectionService.deleteSections(line.getSections());
        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line persistLine = findById(lineId);
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        sectionService.saveSection(persistLine, upStation, downStation, sectionRequest.getDistance());
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = findById(lineId);
        Sections sections = line.getSections();
        Station station = stationService.findById(stationId);
        Section removeTarget = sections.remove(station);
        sectionService.deleteSection(removeTarget);
    }
}
