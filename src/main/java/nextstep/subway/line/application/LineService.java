package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    public static final String NOT_FOUND_LINE = "노선을 찾을 수 없습니다.";
    public static final String NOT_FOUND_STATION = "존재하지 않는 역입니다";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());

        Line line = lineRepository.save(new Line(request, upStation, downStation));

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = getLine(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.updateNameColor(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FOUND_LINE));
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATION));
    }
}
