package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

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

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService,
        SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(
            new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance())
        );

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        return LineResponse.of(findLineByLineId(id));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineByLineId(id);
        line.update(lineRequest.toLine());

        Line persistLine = lineRepository.saveAndFlush(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        Line line = findLineByLineId(id);
        lineRepository.delete(line);
    }

    protected Line findLineByLineId(Long id) {
        return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineByLineId(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);
        sectionRepository.save(section);
        return LineResponse.of(line);
    }

}
