package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return LineResponse.ofList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineByName(String name) {
        return lineRepository.findLineByName(name)
                .map(LineResponse::of)
                .orElseThrow(() -> new NoResultException(name + "은(는) 존재하지 않습니다"));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new NoResultException(id + "엔티티가 존재하지 않습니다"));
        findLine.update(lineRequest.toLine());
    }

    public void removeLine(Long id) {
        lineRepository.deleteById(id);
    }
}
