package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    private Station findStationById(Long id) {
       return stationRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine());
        persistLine.setSections(new Section(0, null, upStation));
        persistLine.setSections(new Section(request.getDistance(), upStation, downStation));
        List<Station> stations = getStations(persistLine.getSections());
        return LineResponse.of(persistLine, stations);
    }

    public List<LineResponse> findByAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse
                        .of(line, getStations(line.getSections())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line, getStations(line.getSections()));
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line, getStations(line.getSections()));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public List<Station> getStations(List<Section> sections) {
        List<Station> stations = new ArrayList<>();

        Optional<Section> firstSection = sections.stream()
                .filter(section -> section.getUpStation() == null)
                .findFirst();

        while(firstSection.isPresent()) {
            Section nowSection = firstSection.get();
            stations.add(nowSection.getStation());
            firstSection = sections.stream()
                    .filter(section -> section.getUpStation() == nowSection.getStation())
                    .findFirst();
        }
        return stations;
    }

    public LineResponse addStation(Long id, SectionRequest sectionRequest) {
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections();
        sections.forEach(section -> {
            if(section.getUpStation() == upStation) {
                section.updateSection(downStation, section.getStation(), section.getDistance() - sectionRequest.getDistance());
            }
            if(section.getStation() == downStation) {
                section.updateSection(section.getUpStation(), upStation,section.getDistance() - sectionRequest.getDistance());
            }
        });
        line.setSections(new Section(sectionRequest.getDistance(), upStation, downStation)); // section 저장
        return LineResponse.of(line, getStations(sections));
    }
}
