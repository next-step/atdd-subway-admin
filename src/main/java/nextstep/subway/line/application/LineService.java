package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    public static final String COULD_NOT_FIND_LINE = "Could not find line ";
    public static final String COULD_NOT_FIND_STATION = "Could not find station ";

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return getLineResponse(request, persistLine);
    }

    @Transactional
    public LineResponse addSection(Long id, LineRequest request) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_LINE + id));
        return getLineResponse(request, findLine);
    }

    private LineResponse getLineResponse(LineRequest request, Line line) {
        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());
        Section section = new Section(upStation, downStation, request.getDistance());
        section.setLine(line);
        sectionRepository.save(section);
        lineRepository.flush();
        return LineResponse.of(line);
    }

    private Station getStationById(Long stationId) {
        Station station = null;
        if (stationId != null) {
            station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_STATION + stationId));
        }
        return station;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_LINE + id));
        findLine.getSections().sortBy();
        return LineResponse.of(findLine);
    }

    @Transactional
    public LineResponse updateLineById(Long id, LineRequest request) {
        Line updateLine = lineRepository.findById(id)
                .map(line -> {
                    line.update(request.toLine());
                    return lineRepository.save(line);
                })
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_LINE + id));
        return LineResponse.of(updateLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

}
