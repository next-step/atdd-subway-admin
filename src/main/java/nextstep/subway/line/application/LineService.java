package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
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

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

<<<<<<< HEAD
    public LineResponse getOne(Long id) {
        return LineResponse.of(lineRepository.getOne(id));
    }


    public LineResponse modify(Long id, LineRequest lineRequest) {
        Line modifyingItem = lineRepository.getOne(id);
        modifyingItem.update(lineRequest);
        return LineResponse.of(modifyingItem);
    }

    public void delete(Long id) {
        lineRepository.delete(lineRepository.getOne(id));
    }
=======


>>>>>>> feat: 조회 요청 추가
}
