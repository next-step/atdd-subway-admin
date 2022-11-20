package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long saveLine(LineRequest lineRequest) {
        setUpDownStation(lineRequest);

        Line line = lineRequest.toLine();
        line.addDefaultSection(lineRequest.getDistance(), lineRequest.getUpStation(), lineRequest.getDownStation());

        return lineRepository.save(line)
                .getId();
    }

    public SectionResponse findSectionResponsesByLineId(Long lineId) {
        Line line = lineRepository.findWithSectionsByIdOrderBySortNoAsc(lineId)
                .orElseThrow(EntityNotFoundException::new);

        return new SectionResponse(line.getDistances(), line.getStationNames());
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findResponseById(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);

        return LineResponse.of(line);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findWithSectionsByIdOrderBySortNoAsc(lineId)
                .orElseThrow(EntityNotFoundException::new);
        Station upStation = stationService.findEntityById(sectionRequest.getUpStationId());
        Station downStation = stationService.findEntityById(sectionRequest.getDownStationId());

        line.addSection(sectionRequest.getDistance(), upStation, downStation);
    }

    @Transactional
    public void updateLine(String name, LineRequest lineRequest) {
        Line line = lineRepository.findByName(name)
                .orElseThrow(EntityNotFoundException::new);

        line.change(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public void deleteSectionByStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findWithSectionsByIdOrderBySortNoAsc(lineId)
                .orElseThrow(EntityNotFoundException::new);
        Station station = stationService.findEntityById(stationId);

        line.deleteSectionByStation(station);
    }

    private void setUpDownStation(LineRequest lineRequest) {
        lineRequest.setUpStation(stationService.findEntityById(lineRequest.getUpStationId()));
        lineRequest.setDownStation(stationService.findEntityById(lineRequest.getDownStationId()));
    }
}
