package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.NotFoundStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class LineService {
    private static final String NOT_FOUND_STATION_MESSAGE = "해당하는 역을 찾을 수 없습니다.";
    private static final String NOT_FOUND_LINE_MESSAGE = "해당하는 노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(
                new Line(lineRequest.getName()
                        , lineRequest.getColor()
                        , upStation
                        , downStation
                        , lineRequest.getDistance()));

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(() -> new NotFoundLineException(NOT_FOUND_LINE_MESSAGE));
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = findLineById(id);

        line.update(request.getName(), request.getColor());

        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundStationException(NOT_FOUND_STATION_MESSAGE));
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest request) {
        Line line = findLineById(id);
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());
        line.addSection(upStation, downStation, request.getDistance());

        return LineResponse.of(line);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(NOT_FOUND_LINE_MESSAGE));
    }

    @Transactional
    public void deleteLineStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        line.deleteStation(stationId);
    }
}
