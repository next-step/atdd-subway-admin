package nextstep.subway.line.application;

import nextstep.subway.exception.CannotFindEntityException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private static final String ERROR_MESSAGE_CANNOT_FIND_LINE = "요청한 지하철 노선 정보를 찾을 수 없습니다.";
    private static final String ERROR_MESSAGE_CANNOT_FIND_STATION = "요청한 지하철역 정보를 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequestToLine(lineRequest);
        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    private Line lineRequestToLine(LineRequest lineRequest) {
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());

        Section section = Section.of(upStation, downStation, lineRequest.getDistance());

        Line line = Line.of(lineRequest.getName(), lineRequest.getColor());
        line.addSection(section);
        return line;
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new CannotFindEntityException(ERROR_MESSAGE_CANNOT_FIND_STATION));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new CannotFindEntityException(ERROR_MESSAGE_CANNOT_FIND_LINE));

        return Collections.singletonList(LineResponse.of(line));
    }

    public void updateById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new CannotFindEntityException(ERROR_MESSAGE_CANNOT_FIND_LINE));

        line.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
