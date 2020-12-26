package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public List<LineResponse> listLine() {
		List<Line> findLineList = lineRepository.findAll();
		List<LineResponse> lineListResponse = new ArrayList<>();
		for (Line findList : findLineList) {
			lineListResponse.add(LineResponse.of(findList));
		}
		return lineListResponse;
	}

	public LineResponse findLineById(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + id));
		return LineResponse.of(line);
	}

	public void update(LineRequest lineRequest) {
		Line line = lineRepository.findById(lineRequest.getId()).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineRequest.getId()));
		line.update(lineRequest.toLine());
		lineRepository.save(line);
	}

	public void delete(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + id));
		lineRepository.delete(line);
	}
}
