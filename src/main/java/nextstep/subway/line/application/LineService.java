package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    public static final String COULD_NOT_FIND_LINE = "Could not find line ";
    public static final String COULD_NOT_FIND_STATION = "Could not find station ";

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        addStationById(persistLine, request.getUpStationId());
        addStationById(persistLine, request.getDownStationId());
        return LineResponse.of(persistLine);
    }

    private void addStationById(Line persistLine, Long stationId) {
        if (stationId != null) {
            Station upStation = stationRepository.findById(stationId)
                    .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_STATION + stationId));
            upStation.setLine(persistLine);
        }
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
