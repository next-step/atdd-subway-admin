package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return lineRepository.findAll()
			.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findById(Long id) {
		return LineResponse.of(lineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("조회 할 대상이 없습니다.")));
	}

	public LineResponse updateById(Long id, LineRequest lineRequest) {
		Line line  = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("조회 할 대상이 없습니다."));
		line.update(lineRequest.toLine());
		return LineResponse.of(line);
	}

	public void deleteById(Long id) {
		lineRepository.deleteById(id);
	}
}
