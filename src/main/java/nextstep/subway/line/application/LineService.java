package nextstep.subway.line.application;

import java.util.List;

import javax.persistence.EntityNotFoundException;

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
	private SectionRepository sections;

	@Autowired
	private StationRepository stations;

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lines.save(request.toLine());
		Station upStation = stations.findById(request.getUpStationId()).orElseThrow(EntityNotFoundException::new);
		Station downStation = stations.findById(request.getDownStationId()).orElseThrow(EntityNotFoundException::new);
		Section persistSection = sections.save(new Section(persistLine, upStation, downStation, request.getDistance()));
		persistLine.addSection(persistSection);
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return LineResponse.ofList(lines.findAll());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findByIdLine(id));
	}

	public void deleteLineById(Long id) {
		Line deleteLine = lines.findById(id).orElseThrow(EntityNotFoundException::new);
		deleteLine.getSections().stream().forEach(section -> sections.delete(section));
		lines.deleteById(id);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findByIdLine(id);
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	private Line findByIdLine(Long id) {
		return lines.findById(id).orElseThrow(EntityNotFoundException::new);
	}

}
