package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    public static final String NOT_FOUND_LINE = "지하철노선을 찾을 수 없습니다. (lineId = %s)";
    public static final String NOT_FOUND_STATION = "지하철역을 찾을 수 없습니다. (stationId = %s)";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        line.addSection(parseToSection(lineRequest));
        Line savedLine = lineRepository.save(line);

        return LineResponse.of(savedLine, generateStationResponse(savedLine));
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(line -> LineResponse.of(line, generateStationResponse(line)))
            .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long lineId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new NoSuchElementException(String.format(NOT_FOUND_LINE, lineId)));
        return LineResponse.of(line, generateStationResponse(line));
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new NoSuchElementException(String.format(NOT_FOUND_LINE, lineId)));

        line.update(lineRequest.toLine());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new NoSuchElementException(String.format(NOT_FOUND_LINE, lineId)));

        line.addSection(sectionRequest.toSection());
    }

    @Transactional
    public void removeStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new NoSuchElementException(String.format(NOT_FOUND_LINE, lineId)));

        line.removeStation(Station.from(stationId));
    }

    private List<StationResponse> generateStationResponse(Line line) {
        LineStations lineStations = line.findSortedLineStations();
        return lineStations.convertToStationResponse();
    }

    private Section parseToSection(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        return Section.of(upStation, downStation, Distance.from(lineRequest.getDistance()));
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(NOT_FOUND_STATION, stationId))
            );
    }
}
