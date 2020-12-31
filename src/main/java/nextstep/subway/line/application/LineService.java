package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 모든 지하철 노선을 검색합니다.
     * @return
     */
    public List<LineResponse> findAllLines() {
        return this.lineRepository.findAll().stream()
                            .map(LineResponse::of)
                            .collect(Collectors.toList());
    }

    /**
     * ID로 지하철 노선을 검색합니다.
     * @param id
     * @return
     */
    public Optional<LineResponse> findLine(Long id) {
        return this.lineRepository.findById(id).map(LineResponse::of);
    }

    /**
     * ID로 지하철 노선을 삭제합니다.
     * @param id
     */
    public void deleteLine(Long id) {
        this.lineRepository.delete(this.lineRepository.getOne(id));
    }

    /**
     * 해당 ID로 지하철 노선이 존재하는지 여부를 반환합니다.
     * @param id
     * @return
     */
    public boolean existLineById(Long id) {
        return !this.findLine(id).isPresent();
    }
}
