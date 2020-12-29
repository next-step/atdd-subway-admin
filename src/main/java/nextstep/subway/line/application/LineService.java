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

        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections();
        Section newSection = validationCheck(sectionRequest, sections);
        sections.forEach(section -> {
            if (section.getUpStation() == newSection.getUpStation()) {
                newSection.setDistance(newSection.getDistance() - section.getDistance());
                section.updateSection(newSection.getStation(), section.getStation(), section.getDistance());
            }
            if (section.getStation() == newSection.getStation()) {
                newSection.setDistance(newSection.getDistance() - section.getDistance());
                section.updateSection(section.getUpStation(), newSection.getUpStation(), section.getDistance());
            }
        });
        line.setSections(newSection);
        return LineResponse.of(line, getStations(sections));
    }

    private Section validationCheck(SectionRequest sectionRequest, List<Section> sections) {
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Section newSection = new Section(sectionRequest.getDistance(), upStation, downStation);

        if (sections.stream().anyMatch(section -> section.equals(newSection))) {
            throw new RuntimeException("동일한 구간 추가 요청");
        }
        if (sections.stream().noneMatch(section -> section.hasStation(newSection))) {
            throw new RuntimeException("등록되어 있지 않은 구간 추가 요청");
        }
        if (sections.stream()
                .filter(section -> section.getUpStation() == newSection.getUpStation())
                .anyMatch(section -> section.getDistance() <= newSection.getDistance())) {
            throw new RuntimeException("구간 길이 오류");
        }
        if (sections.stream()
                .filter(section -> section.getStation() == newSection.getStation())
                .anyMatch(section -> section.getDistance() <= newSection.getDistance())) {
            throw new RuntimeException("구간 길이 오류");
        }
        return newSection;
    }
}
