package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }


    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineByid(Long id) {
        return lineRepository.findById(id)
                .map(line -> LineResponse.of(line))
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 해당 노선은 존재하지 않습니다."));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line originLine = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 해당 노선은 존재하지 않습니다."));
        originLine.update(lineRequest);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
