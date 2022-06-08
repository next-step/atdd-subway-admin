package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    public static final String ERROR_MESSAGE_UP_STATION_NOT_EXISTS = "상행종점 역이 존재하지 않습니다.";
    public static final String ERROR_MESSAGE_DOWN_STATION_NOT_EXISTS = "하행종점 역이 존재하지 않습니다.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());

        Station upStation = stationRepository.getById(lineRequest.getUpStationId());
        Station downStation = stationRepository.getById(lineRequest.getDownStationId());
        validateExistsOfStations(upStation, downStation);

        Section firstSection = new Section(persistLine.getId(), upStation, downStation, lineRequest.getDistance());
        persistLine.addSection(firstSection);

        return LineResponse.from(persistLine);
    }

    private void validateExistsOfStations(Station upStation, Station downStation) {
        validateExistsOfStation(upStation, ERROR_MESSAGE_UP_STATION_NOT_EXISTS);
        validateExistsOfStation(downStation, ERROR_MESSAGE_DOWN_STATION_NOT_EXISTS);
    }

    private void validateExistsOfStation(Station station, String errorMessage) {
        if (station == null) {
            throw new DataIntegrityViolationException(errorMessage);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line line = lineRepository.getById(id);
        return LineResponse.from(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.getById(id);
        line.updateName(lineRequest.getName());
        line.updateColor(lineRequest.getColor());
    }
}
