package nextstep.subway.line.application;

import nextstep.subway.exception.DuplicateValueException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    public static final String DUPLICATE_VALUE_ERROR_MESSAGE = "%s 라인은 이미 등록된 라인 합니다.";
    public static final String NOT_FOUND_STATION_ERROR_MESSAGE = "아이디 %s와 일치하는 역 정보가 존재하지 않습니다.";
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        try {
            Station upStation = findStation(request.getUpStationId());
            Station downStation = findStation(request.getDownStationId());
            Section section = request.toSection(upStation, downStation);
            Line line = request.toLine();
            line.addSection(section);
            return LineResponse.of(lineRepository.save(line));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateValueException(String.format(DUPLICATE_VALUE_ERROR_MESSAGE, request.getName()));
        }
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() ->  new IllegalArgumentException(String.format(NOT_FOUND_STATION_ERROR_MESSAGE)));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        return LineResponse.of(line);
    }

    public void updateLineById(Long id, LineRequest lineRequest) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        line.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        lineRepository.delete(line);
    }
}
