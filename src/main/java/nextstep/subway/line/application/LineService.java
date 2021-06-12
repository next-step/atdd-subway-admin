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
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
	private LineRepository lines;
	private StationRepository stations;
	private SectionRepository sections;

	public LineService(LineRepository lines, StationRepository stations, SectionRepository sections) {
		this.lines = lines;
		this.stations = stations;
		this.sections = sections;
	}

	public LineResponse saveLine(LineRequest request) {
		Station startStation = getTerminalStation(request.getUpStationId());
		Station endStation = getTerminalStation(request.getDownStationId());
		Line line = request.toLine();
		line.addSection(request.toSection(line, startStation, endStation));

		Line persistLine = lines.save(line);
		return LineResponse.of(persistLine);
	}

	private Station getTerminalStation(Long id) {
		return stations.findById(id)
			.orElseThrow(() -> new NoSuchElementException("Terminal station is needed"));
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
		lines.delete(line);
	}
}
