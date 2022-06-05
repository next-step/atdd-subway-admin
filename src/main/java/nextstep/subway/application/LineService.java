package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
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

    public LineService(final LineRepository lineRepository,
                       final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Line line = request.toLine();
        line.setFinalStations(
                stationService.getStationOrElseThrow(request.getUpStationId()),
                stationService.getStationOrElseThrow(request.getDownStationId()),
                request.getDistance());
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = getLineOrElseThrow(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(final Long id, final LineRequest lineRequest) {
        final Line line = getLineOrElseThrow(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.save(line);
    }

    @Transactional
    public void deleteLineById(final Long id) {
        final Line line = getLineOrElseThrow(id);
        lineRepository.delete(line);
    }

    @Transactional
    public SectionResponse addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Line line = getLineOrElseThrow(lineId);
        final Section section = line.relateToSection(
                stationService.getStationOrElseThrow(sectionRequest.getUpStationId()),
                stationService.getStationOrElseThrow(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
        lineRepository.save(line);
        return SectionResponse.of(section);
    }

    public List<SectionResponse> findAllSections(final Long id) {
        return getLineOrElseThrow(id).getSections().sections();
    }

    private Line getLineOrElseThrow(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("지하철 노선 아이디가 유효하지 않습니다: %d}", id)));
    }
}
