package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.line.dto.LineSectionCreateRequest;
import nextstep.subway.section.dto.SectionAddRequest;
import nextstep.subway.section.dto.SectionAddResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class SectionService {


    private StationService stationService;
    private SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public void saveSection(LineSectionCreateRequest request) {
        Station up = stationService.getOne(request.getUpStationId());
        Station down = stationService.getOne(request.getDownStationId());
        sectionRepository.save(new Section(request.getLine(), up, down, request.getDistance(), request.isStart()));
    }

    public void delete(List<Section> sections) {
        sectionRepository.deleteAll(sections);
    }

    public Section getStart(Line line) {
        return sectionRepository.getStart(line);
    }

    public Section getByStartStation(Line line, Station station) {
        return sectionRepository.getByStation(line, station);
    }

    public List<Section> getOrderedSections(Line line) {
        List<Section> sections = new ArrayList<>();
        Section section = getStart(line);
        while (section != null) {
            sections.add(section);
            section = getByStartStation(line, section.getDown());
        }
        return sections;
    }

    public SectionAddResponse addSection(Line addingLine, SectionAddRequest sectionAddRequest) {
        Station up = stationService.getOne(sectionAddRequest.getUpStationId());
        Station down = stationService.getOne(sectionAddRequest.getDownStationId());
        int distance = sectionAddRequest.getDistance();
        List<Section> sections = getOrderedSections(addingLine);
        if (isTopEndSection(up, down, distance, sections)) {
            return SectionAddResponse.of(sectionRepository.save(new Section(addingLine, up, down, distance, true)));
        }
        if (isBottomEndSection(up, down, distance, sections)) {
            return SectionAddResponse.of(sectionRepository.save(new Section(addingLine, up, down, distance)));
        }
        return sections.stream()
                .map(item -> item.createAndChange(up, down, distance))
                .filter(Objects::nonNull)
                .findFirst()
                .map(value -> SectionAddResponse.of(sectionRepository.save(value)))
                .orElseGet(() -> {
                    throw new IllegalArgumentException("Station is invalid");
                });
    }

    private boolean isBottomEndSection(Station up, Station down, int distance, List<Section> sections) {
        if (sections.get(sections.size() - 1).getDown().equals(up)) {
            return true;
        }
        return false;
    }

    private boolean isTopEndSection(Station up, Station down, int distance, List<Section> sections) {
        if (sections.get(0).getUp().equals(down)) {
            sections.forEach(Section::setNotStart);
            return true;
        }
        return false;
    }


}
