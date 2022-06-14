package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    public static final String ERROR_MESSAGE_UP_STATION_NOT_EXISTS = "상행종점 역이 존재하지 않습니다.";
    public static final String ERROR_MESSAGE_DOWN_STATION_NOT_EXISTS = "하행종점 역이 존재하지 않습니다.";
    public static final String ERROR_MESSAGE_DELETE_STATION_NOT_EXISTS = "삭제할 역이 존재하지 않습니다.";
    public static final String ERROR_MESSAGE_LINE_NOT_EXISTS = "해당 노선이 존재하지 않습니다.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());

        addSection(persistLine, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());

        return LineResponse.from(persistLine);
    }

    public void addSectionByRequest(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_LINE_NOT_EXISTS));

        addSection(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
    }

    private void addSection(Line line, Long upStationId, Long downStationId, Integer distance) {
        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_UP_STATION_NOT_EXISTS));
        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_DOWN_STATION_NOT_EXISTS));

        Section section = new Section(line.getId(), upStation, downStation, distance);
        line.addSection(section);
    }

    public void removeSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_LINE_NOT_EXISTS));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_DELETE_STATION_NOT_EXISTS));

        line.removeSection(station);
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
