package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.section.domain.Sections;
import nextstep.subway.sectionstations.domain.SectionStation;
import nextstep.subway.sectionstations.domain.SectionStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.dto.StationResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    public static final String NOT_FOUND_LINE = "노선을 찾을 수 없습니다.";
    public static final String NOT_FOUND_STATION = "존재하지 않는 역입니다";

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;
    private final SectionStationRepository sectionStationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository, SectionStationRepository sectionStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
        this.sectionStationRepository = sectionStationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine());

        Section section = Section.getInstance(persistLine, upStation, downStation, request.getDistance());
        sectionRepository.save(section);

        sectionStationRepository.save(new SectionStation(section, upStation));
        sectionStationRepository.save(new SectionStation(section, downStation));

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
        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        Line line = getLine(id);
        Sections sections = line.getSections();
        sections.getSections().forEach(sectionRepository::delete);

        lineRepository.deleteById(id);
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FOUND_LINE));
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATION));
    }
}
