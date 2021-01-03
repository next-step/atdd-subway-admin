package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.selectStationById(request.getUpStationId());
        Station downStation = stationService.selectStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(selectLineById(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = selectLineById(id);
        persistLine.update(lineRequest.toLine());
    }

    private Line selectLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = selectLineById(id);
        Section newSection = toSection(line, sectionRequest);
        line.addSection(newSection);
        return LineResponse.of(lineRepository.save(line));
    }

    private Section toSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.selectStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.selectStationById(sectionRequest.getDownStationId());

        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(sectionRequest.getDistance()))
                .build();
    }

}
