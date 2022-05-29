package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.exception.NoSuchElementFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementFoundException("해당 노선을 찾을 수 없습니다."));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(LineUpdateRequest updateRequest) {
        Line line = lineRepository.findById(updateRequest.getId()).orElseThrow(() -> new NoSuchElementFoundException("해당 노선을 찾을 수 없습니다."));
        line.updateLine(updateRequest.getName(), updateRequest.getColor());
    }

    @Transactional
    public void deleteLineById(@PathVariable Long id) {
        if (!lineRepository.existsById(id)) {
            throw new NoSuchElementFoundException("해당 노선을 찾을 수 없습니다.");
        }
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new NoSuchElementFoundException("해당 역을 찾을 수 없습니다."));
    }
}
