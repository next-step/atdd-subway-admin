package nextstep.subway.line.application;

import nextstep.subway.exception.ApiException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_LINE;
import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_STATION;

@Service
@Transactional
public class LineService {
    private final LineRepository lines;
    private final StationRepository stations;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lines = lineRepository;
        this.stations = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lines.save(makeLine(request));
        return LineResponse.of(persistLine);
    }

    private Line makeLine(final LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line line = request.toLine();
        line.addSection(new Section(upStation, downStation, request.getDistance()));
        return line;
    }

    private Station findStationById(final Long id) {
        return stations.findById(id).orElseThrow(() -> new ApiException(NOT_FOUND_STATION));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        return convertLineResponse(lines.findAll());
    }

    private List<LineResponse> convertLineResponse(List<Line> lines) {
        return lines.stream()
                    .map(LineResponse::of)
                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(final Long id) {
        return lines.findById(id)
                    .map(LineResponse::of)
                    .orElseThrow(() -> new ApiException(NOT_FOUND_LINE));
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest request) {
        Line line = lines.findById(id)
                         .orElseThrow(() -> new ApiException(NOT_FOUND_LINE));
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLine(final Long id) {
        lines.deleteById(id);
    }
}
