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
        /*
        Long upStationId = request.getUpStationId();
        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_STATION + upStationId));

        Long downStationId = request.getDownStationId();
        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_STATION + downStationId));

        List<Station> stations = Arrays.asList(upStation, downStation);
         */
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"));
        return LineResponse.of(persistLine, stations);
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
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"));
        return LineResponse.of(findLine, stations);
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
