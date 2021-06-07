package nextstep.subway.line.application;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

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
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findByIdLine(id));
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findByIdLine(id);
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	private Line findByIdLine(Long id) {
		return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

}
