package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
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
	private LineRepository lines;

	public LineService(LineRepository lines) {
		this.lines = lines;
	}

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lines.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		List<Line> lines = this.lines.findAll();

		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLine(Long id) {
		return LineResponse.of(lines.findById(id).get());
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line line = lines.findById(id).orElseThrow(() -> new NoSuchElementException("There is no line for the id"));
		line.update(lineRequest.toLine());
	}

	public void deleteLineById(Long id) {
		Line line = lines.findById(id).orElseThrow(() -> new NoSuchElementException("There is no line for the id"));
		lines.deleteById(line.getId());
	}
}
