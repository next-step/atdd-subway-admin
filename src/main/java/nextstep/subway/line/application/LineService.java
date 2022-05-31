package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    public static final String NO_LINE_ERROR = "접근 하는 노선이 존재 하지 않습니다.";
    public static final String NO_STATION_ERROR = "접근 하는 지하철역이 존재 하지 않습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = convertToLine(request);
        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    private Line convertToLine(LineRequest request) {
        return Line.of(request.getName(), request.getColor(), makeSectionFromLineRequest(request));
    }

    private Section makeSectionFromLineRequest(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());
        return Section.of(upStation, downStation, request.getDistance());
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(NO_STATION_ERROR)
        );
    }

    public List<LineResponse> findAllLines() {
        List<Line> findLines = lineRepository.findAll();
        return findLines.stream().
                map(LineResponse::of).
                collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(NO_LINE_ERROR)
        );
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(NO_LINE_ERROR)
        );
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line findLine = lineRepository.findById(lineId).orElseThrow(
                () -> new NoSuchElementException(NO_LINE_ERROR)
        );
        Station upStation = getStation(sectionRequest.getUpStationId());
        Station downStation = getStation(sectionRequest.getDownStationId());
        findLine.addSection(Section.of(upStation, downStation, sectionRequest.getDistance()));
    }
}
