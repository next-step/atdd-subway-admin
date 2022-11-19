package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequestToLine(lineRequest));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return lineRepository.findById(id).map(LineResponse::of)
                .orElseThrow(() -> new NoSuchElementException("주어진 id로 생성된 지하철호선이 없습니다."));
    }

    private Line lineRequestToLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor(),
                stationRepository.getById(lineRequest.getUpStationId()),
                stationRepository.getById(lineRequest.getDownStationId()),
                lineRequest.getDistance());
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("주어진 id로 생성된 지하철호선이 없습니다."));
        lineRepository.save(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor(),
                line.getUpStation(), line.getDownStation(), line.getDistance()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
