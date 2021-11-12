package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

@Service
@Transactional
public class LineService {
    public static final String NOT_EXIST_LINE = "id=%s 에 해당하는 노선이 존재하지 않습니다.";
    public static final String NOT_EXIST_STATION = "id=%s 에 해당하는 역이 존재하지 않습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        line.addSection(buildSection(request));
        Line savedLine = lineRepository.save(line);

        return LineResponse.of(savedLine, createStationResponses(savedLine));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                    .map(line -> LineResponse.of(line, createStationResponses(line)))
                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line, createStationResponses(line));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        throw new IllegalStateException();
    }

    private Section buildSection(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        return Section.of(upStation, downStation, Distance.from(request.getDistance()));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                             .orElseThrow(() -> new NoSuchElementException(String.format(NOT_EXIST_LINE, id)));
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                                .orElseThrow(() -> new NoSuchElementException(String.format(NOT_EXIST_STATION, id)));
    }

    private List<StationResponse> createStationResponses(Line line) {
        return line.getStations()
                   .stream()
                   .map(StationResponse::from)
                   .collect(Collectors.toList());
    }
}
