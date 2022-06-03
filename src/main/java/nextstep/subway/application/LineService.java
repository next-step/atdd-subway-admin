package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.InvalidLineException;
import nextstep.subway.exception.InvalidStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private static final String INVALID_STATION = "유효하지 않은 지하철역입니다.";
    private static final String INVALID_LINE = "%d : 유효하지 않은 지하철 노선입니다.";

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        Long distance = lineRequest.getDistance();
        Line newLine = lineRepository.save(new Line(name, color, upStation, downStation, distance));
        return LineResponse.of(newLine);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Optional<Line> lineOptional = lineRepository.findById(id);
        String lineName = lineOptional.orElse(null).getName();
        Line foundLine = lineRepository.findById(id).orElseThrow(
            () -> new InvalidLineException(String.format(INVALID_LINE, lineName))
        );
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Station upStation = findStationById(foundLine.getUpStation().getId());
        Station downStation = findStationById(foundLine.getDownStation().getId());
        Long distance = lineRequest.getDistance();
        foundLine.update(name, color, upStation, downStation, distance);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Optional<Line> lineOptional = lineRepository.findById(id);
        String lineName = lineOptional.orElse(null).getName();
        return LineResponse.of(lineOptional
            .orElseThrow(() -> new InvalidLineException(String.format(INVALID_LINE, lineName))));
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new InvalidStationException(INVALID_STATION));
    }

}
