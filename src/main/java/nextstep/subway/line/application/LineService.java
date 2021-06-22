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
	private LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		checkDuplicated(request);
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		Optional<Line> line = lineRepository.findById(id);
		return LineResponse.of(line.get());
	}

	public void updateLine(Long id, LineRequest request){
		Optional<Line> line = lineRepository.findById(id);
		if (!line.isPresent()) {
			throw new IllegalArgumentException("노선이 존재하지 않습니다.");
		}

		line.get().update(request.toLine());
	}

	private void checkDuplicated(LineRequest request) {
		if (lineRepository.findByName(request.getName()).isPresent()) {
			throw new IllegalArgumentException("노선이 이미 존재합니다.");
		}
	}
}
