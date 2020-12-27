package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.*;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineByName(String name) {
        return LineResponse.of(lineRepository.findLineByName(name)
                .orElseThrow(() -> new NoResultException(name + "은(는) 존재하지 않습니다")));
    }

    public void updateLine(Line line) {
        // 변경 감지 방법으로 수정한다.
        Line findLine = lineRepository.findById(line.getId())
                .orElseThrow(() -> new NoResultException(line.getName() + "을 수정할 수 없습니다"));
        findLine.update(line);
    }

    public void removeLine(Long id) {
        lineRepository.deleteById(id);
    }
}
