package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.dto.StationResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    public static final String NOT_FOUND_LINE = "노선을 찾을 수 없습니다.";
    public static final String NOT_FOUND_STATION = "존재하지 않는 역입니다";

    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine());

        Section section = new Section();
        section.setLine(persistLine);
        section.setPrevStation(upStation);
        section.setNextStation(downStation);
        section.setDistance(request.getDistance());

        sectionRepository.save(section);
        persistLine.addSection(section);

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = getLine(id);

        List<Section> sections = sectionRepository.findByLineId(id);
        List<StationResponse> stationResponses = new ArrayList<>();

        for (Section section : sections) {
            addStationResponse(stationResponses, section);
        }

        return LineResponse.of(line, stationResponses);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        Line line = getLine(id);
        Sections sections = line.getSections();
        sections.getSections().forEach(section -> sectionRepository.delete(section));

        lineRepository.deleteById(id);
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FOUND_LINE));
    }

    private void addStationResponse(List<StationResponse> stationResponses, Section section) {
        addResponse(stationResponses, StationResponse.of(section.getPrevStation()));
        addResponse(stationResponses, StationResponse.of(section.getNextStation()));
    }

    private void addResponse(List<StationResponse> stationResponses, StationResponse stationResponse) {
        if (!stationResponses.contains(stationResponse)) {
            stationResponses.add(stationResponse);
        }
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATION));
    }
}
