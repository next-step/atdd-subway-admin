package nextstep.subway.application;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.repository.SectionRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService,
                       SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public Long saveLine(LineRequest lineRequest) {
        setUpDownStation(lineRequest);
        Line persistLine = lineRepository.save(lineRequest.toLine());
        sectionRepository.save(new Section(persistLine, lineRequest.getDistance(), lineRequest.getUpStation(),
                lineRequest.getDownStation()));
        return persistLine.getId();
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(this::getLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return getLineResponse(line);
    }

    public LineResponse findByName(String name) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return getLineResponse(line);
    }

    @Transactional
    public void updateLine(String name, LineRequest lineRequest) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        line.change(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        sectionRepository.findByLineId(lineId).ifPresent(sections -> sections.forEach(
                sectionRepository::delete));
        lineRepository.deleteById(lineId);
    }

    private LineResponse getLineResponse(Line line) {
        return LineResponse.of(line);
    }

    private void setUpDownStation(LineRequest lineRequest) {
        List<Station> stations = stationService
                .findStationsByIdIn(Sets.newHashSet(lineRequest.getUpStationId(), lineRequest.getDownStationId()));
        lineRequest.isUpStationThenSet(stations);
        lineRequest.isDownStationThenSet(stations);
    }
}
