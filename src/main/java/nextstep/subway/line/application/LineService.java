package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        sectionService.saveSection(
            new SectionRequest(request.getUpStationId(), request.getDownStationId(), request.getDistance()),
            persistLine);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAllByWithSections();
        return convertToLineResponses(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        Line findLine = findLineByIdWithSections(id);
        return LineResponse.of(findLine,
            convertToStationResponse(findLine.getSections().createStations()));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line findLine = findLineById(id);
        findLine.update(lineRequest.toLine());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line findLine = findLineById(id);
        lineRepository.delete(findLine);
    }

    static List<StationResponse> convertToStationResponse(List<Station> stations) {
        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    private List<LineResponse> convertToLineResponses(List<Line> lines) {
        List<LineResponse> responseLines = new ArrayList<>();
        for (Line line : lines) {
            responseLines.add(LineResponse.of(line,
                convertToStationResponse(line.getSections().createStations())));
        }
        return responseLines;
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    private Line findLineByIdWithSections(Long id) {
        return lineRepository.findByIdWithSections(id)
            .orElseThrow(NotFoundException::new);
    }
}
