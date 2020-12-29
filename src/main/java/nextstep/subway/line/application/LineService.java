package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

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

	public List<LineResponse> findAllLines() {
		List<Line> persistLine = lineRepository.findAll();
		return persistLine.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		Line persistLine = lineRepository.findById(id).orElseThrow(() -> new IllegalStateException("등록된 노선이 아닙니다."));
		return LineResponse.of(persistLine);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line persistLine = lineRepository.findById(id).orElseThrow(() -> new IllegalStateException("등록된 노선이 아닙니다."));
		persistLine.update(lineRequest.toLine());
	}

	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}
}
