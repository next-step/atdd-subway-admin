package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
	@Autowired
	private LineRepository lines;

	@Autowired
	private StationRepository stations;

	@Autowired
	private SectionRepository sections;

	public LineResponse saveLine(LineRequest request) {
		Station startStation = stations.findById(request.getUpStationId()).get();
		Station endStation = stations.findById(request.getDownStationId()).get();
		Line persistLine = lines.save(request.toLine(startStation));
		Section persistSections = sections.save(getSection(request, persistLine));
		persistLine.addSections(persistSections);

		return LineResponse.of(persistLine);
	}

	private Section getSection(LineRequest request, Line persistLine) {
		return request.toSection(persistLine, getStation(request.getUpStationId()),
			getStation(request.getDownStationId()));
	}

	private Station getStation(Long id) {
		return stations.findById(id).get();
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
