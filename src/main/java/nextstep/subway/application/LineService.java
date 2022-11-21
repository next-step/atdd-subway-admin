package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.*;
import nextstep.subway.exception.NotFoundLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveStation(LineSaveRequest lineSaveRequest) {
        Station upStation = stationService.findStation(lineSaveRequest.getUpStationId());
        Station downStation = stationService.findStation(lineSaveRequest.getDownStationId());
        Line line = lineSaveRequest.toLine();
        line.addStations(upStation, downStation, lineSaveRequest.getDistance());
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundLine(id));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundLine(id));
        line.update(lineUpdateRequest);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundLine(id));
        lineRepository.delete(line);
    }

    @Transactional
    public LineResponse createSection(Long lineId, SectionSaveRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NotFoundLine(lineId));
        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        line.addStations(upStation, downStation, sectionRequest.getDistance());
        return LineResponse.of(line);
    }

    public SectionsResponse findSectionByLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NotFoundLine(lineId));
        return SectionsResponse.of(line);
    }

    @Transactional
    public void deleteSection(Long lineId, Long deleteStationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NotFoundLine(lineId));
        Station deleteStation = stationService.findStation(deleteStationId);
        line.deleteSection(deleteStation);
    }
}
