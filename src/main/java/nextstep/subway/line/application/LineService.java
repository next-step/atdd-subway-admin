package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
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
		Station startStation = getStation(request.getUpStationId());
		Station endStation = getStation(request.getDownStationId());
		Line line = request.toLine();
		line.addSection(request.toSection(line, startStation, endStation));

		Line persistLine = lines.save(line);
		return LineResponse.of(persistLine);
	}

	private Station getStation(Long id) {
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
		Line line = getLine(id);
		line.update(lineRequest.toLine());
	}

	public void deleteLineById(Long id) {
		Line line = getLine(id);
		lines.delete(line);
	}

	public Section addSectionAndReturnNewSection(Long lineId, SectionRequest sectionRequest) {
		Line line = getLine(lineId);
		Station startStation = getStation(sectionRequest.getUpStationId());
		Station endStation = getStation(sectionRequest.getDownStationId());
		Section newSection = new Section(line, startStation, endStation, sectionRequest.getDistance());
		line.addSection(newSection);

		return newSection;
	}

	private Line getLine(Long id) {
		return lines.findById(id).orElseThrow(() -> new NoSuchElementException("There is no line for the id"));
	}

	public void removeStation(Long lineId, Long stationId) {
		Line line = getLine(lineId);
		Station deleteStation = getStation(stationId);
		line.remove(deleteStation);
	}
}
