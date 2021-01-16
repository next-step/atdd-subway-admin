package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.LineStation;
import nextstep.subway.station.domain.LineStationRepository;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    private final LineStationRepository lineStationRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        if (request.getUpStation() != null && request.getDownStation() != null) {
            //세션정보 입력
            Section section = new Section(request.getUpStation(), request.getDownStation(), request.getDistance());
            persistLine.addSection(section);
        }
        LineStation lineStation = new LineStation();
        lineStation.setLine(persistLine);
        lineStationRepository.save(lineStation);
        //final Line result = lineRepository.save(persistLine);
        //return LineResponse.of(result);
        return LineResponse.of(persistLine);
    }

    // 비즈니스 로직 도메인으로 이동 인자만 전달
    public LineResponse saveSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        Section newSection = sectionRequest.toSection();
        line.addSection(newSection);
        LineStation lineStation = new LineStation();
        lineStation.setLine(line);
        lineStationRepository.save(lineStation);
        return LineResponse.of(lineStation.getLine());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        Line lineStation = lineStationRepository.findByLine(line).getLine();
        return LineResponse.of(lineStation);
    }

    public LineResponse updateLineById(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        line.changeLine(request.getName(), request.getColor());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
