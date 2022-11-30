package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(Line.of(lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getDistance(),
                stationService.findById(lineRequest.getUpStationId()),
                stationService.findById(lineRequest.getDownStationId())));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElse(null);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id).orElse(null);
        line.updateNameColor(request.getName(), request.getColor());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElse(null);
        line.addSection(toSection(sectionRequest));
    }

    private Section toSection(SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        return Section.of(upStation, downStation, sectionRequest.getDistance());
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElse(null);
        line.removeStation(stationService.findById(stationId));
    }
}
