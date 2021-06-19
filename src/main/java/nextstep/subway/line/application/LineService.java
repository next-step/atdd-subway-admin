package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.AlreadyExistLineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {
	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private SectionService sectionService;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Optional<Line> findLine = lineRepository.findByName(request.getName());
		if (findLine.isPresent()) {
			throw new AlreadyExistLineException("이미 등록된 노선 정보입니다.");
		}
		Line line = Line.create(request.getName(), request.getColor());

		sectionService.create(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());

		Line newLine = lineRepository.save(line);

		return LineResponse.of(newLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAll() {
		List<Line> lines = lineRepository.findAll();

		List<LineResponse> lineResponses = new ArrayList<>();
		for (Line line : lines) {
			lineResponses.add(LineResponse.of(line));
		}

		return lineResponses;
	}

	public LineResponse findByName(String name) {
		Line line = checkExistLine(name);

		return LineResponse.of(line);
	}

	public LineResponse updateByName(LineRequest req) {
		Line line = checkExistLine(req.getName());

		line.updateColor(req.getColor());

		return LineResponse.of(line);
	}

	public void deleteByName(String name) {
		Optional<Line> line = lineRepository.findByName(name);

		line.ifPresent(value -> lineRepository.delete(value));
	}

	private Line checkExistLine(String name) {
		Optional<Line> byName = lineRepository.findByName(name);
		if (!byName.isPresent()) {
			throw new IllegalArgumentException("해당 노선을 찾을 수 없습니다.");
		}

		return byName.get();
	}
}
