package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.error.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


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
        Station upStation = stationService.findStationEntityById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationEntityById(lineRequest.getDownStationId());
        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        line.initSections(new Section(upStation, downStation, lineRequest.getDistance(), line));

        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse getLineById(Long lineId) {
        Line line = getLineEntity(lineId);
        return new LineResponse(line);
    }

    private Line getLineEntity(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(lineId));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse updateLineById(Long lineId, LineUpdateRequest lineUpdateRequest) {
        Line line = getLineEntity(lineId);
        line.update(lineUpdateRequest);
        return new LineResponse(line);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = getLineEntity(lineId);
        Station upStation = stationService.findStationEntityById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationEntityById(sectionRequest.getDownStationId());
        Section section = new Section(upStation, downStation, sectionRequest.getDistance(), line);
        line.addSection(section);
    }
}
