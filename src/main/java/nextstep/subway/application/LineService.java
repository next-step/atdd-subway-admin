package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE = "해당 라인 ID에 값이 없습니다.";
    private static final String ERROR_MESSAGE_NOT_FOUND_STATION = "해당 역 ID에 값이 없습니다.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(line -> LineResponse.of(line)).collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = getLine(id);
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.changeName(lineRequest.getName());
        line.changeColor(lineRequest.getColor());
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = getLine(lineId);
        Station upStation = getStation(sectionRequest.getUpStationId());
        Station downStation = getStation(sectionRequest.getDownStationId());
        line.addSection(new Section(upStation, downStation, new Distance(sectionRequest.getDistance())));
        return LineResponse.of(line);
    }

    private Line getLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE));
        return line;
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_STATION));
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = getStation(stationId);

        line.removeSection(station);
    }
}
