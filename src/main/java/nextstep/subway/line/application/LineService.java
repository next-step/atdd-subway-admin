package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(
            LineRepository lineRepository, StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line savedLine = lineRepository.save(request.toLine());

        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new StationNotExistException("존재하지 않는 역을 상행종점으로 등록할 수 없습니다."));
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new StationNotExistException("존재하지 않는 역을 하행종점으로 등록할 수 없습니다."));;

        Section section = new Section(upStation.getId(), downStation.getId(), request.getDistance());

        return LineResponse.of(savedLine, Arrays.asList(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(it -> LineResponse.of(it))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));

        return LineResponse.of(line);
    }

    // TODO: 향후에는 Station까지 변경될 경우 변경 예정
    public Line updateLine(Long lineId, String changeName, String changeColor) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        Line updateLine = new Line(changeName, changeColor);
        line.update(updateLine);

        return line;
    }

    public void deleteLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        sectionRepository.deleteAllByLine(line);
        lineRepository.deleteById(lineId);
    }
}
