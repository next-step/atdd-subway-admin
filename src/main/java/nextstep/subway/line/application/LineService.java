package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class LineService {

	private LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public List<LineResponse> listLine() {
		List<Line> findLineList = lineRepository.findAll();
		return findLineList.stream().map(r -> LineResponse.of(r)).collect(Collectors.toList());
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

	public LineResponse saveLine(LineRequest lineRequest, Station upStation, Station downStation) {
		List<Station> stations = Stream.of(upStation, downStation).collect(Collectors.toList());
		Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance(), stations);
		lineRepository.save(line);
		return LineResponse.of(line);
	}
}
