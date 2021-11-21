package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String MESSAGE_ON_UP_STATION_NOT_FOUND = "상행역을 찾을 수 없습니다.";
    private static final String MESSAGE_ON_DOWN_STATION_NOT_FOUND = "하행역을 찾을 수 없습니다.";
    private static final String MESSAGE_ON_LINE_NOT_FOUND = "노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(
        LineRepository lineRepository,
        StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest request) {
        Station upStation = findStationById(request.getUpStationId(), MESSAGE_ON_UP_STATION_NOT_FOUND);
        Station downStation = findStationById(request.getDownStationId(), MESSAGE_ON_DOWN_STATION_NOT_FOUND);

        Line line = lineRepository.save(Line.of(request.getName(), request.getColor()));
        line.addSection(Section.of(upStation.getId(), downStation.getId(), request.getDistance()));

        return LineResponse.of(line, getStationInOrder(line));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.of(line, getStationInOrder(line)))
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        List<Station> stations = getStationInOrder(line);

        return LineResponse.of(line, stations);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = findLineById(id);

        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line line = findLineById(id);

        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(Long id, SectionAddRequest request) {
        Line line = findLineById(id);
        Station upStation = findStationById(request.getUpStationId(), MESSAGE_ON_UP_STATION_NOT_FOUND);
        Station downStation = findStationById(request.getDownStationId(), MESSAGE_ON_DOWN_STATION_NOT_FOUND);

        line.addSection(Section.of(upStation.getId(), downStation.getId(), request.getDistance()));
    }

    private Station findStationById(Long stationId, String messageOnStationNotFound) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new NoSuchElementException(messageOnStationNotFound));
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new NoSuchElementException(MESSAGE_ON_LINE_NOT_FOUND));
    }

    private List<Station> getStationInOrder(Line line) {
        List<Long> stationIds = line.getStationIdsInOrder();
        return stationRepository.findAllById(stationIds);
    }
}
