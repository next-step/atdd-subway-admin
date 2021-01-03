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

	@Transactional(readOnly = true)
	public List<LineResponse> showLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse showLine(Long id) {
		Line persistLine = findLine(id);
		return LineResponse.of(persistLine);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findLine(id);
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id).orElseThrow(RuntimeException::new);
	}

	public void deleteLine(Long id) {
		Line line = findLine(id);
		lineRepository.delete(line);
	}
}
