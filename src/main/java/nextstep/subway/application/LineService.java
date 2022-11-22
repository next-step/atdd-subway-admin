package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LineService {

    private LineRepository lineRepository;

    private SectionRepository sectionRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());

        Line line = lineRequest.toLine();

        Sections sections = new Sections();
        sections.add(new Section(upStation, downStation, line, lineRequest.getDistance()));
        line.addSections(sections);

        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(getLine(id));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse updateById(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        return LineResponse.of(line.update(lineRequest.toLine()));
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.delete(getLine(id));
    }

    public Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Line", id));
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station", id));
    }

}
