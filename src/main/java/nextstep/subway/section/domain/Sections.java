package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    List<Section> sections = new ArrayList<Section>();

    public Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<StationResponse> toResponseList() {
        return sections.stream()
            .map(section -> Stream.of(section.upStationToReponse(), section.downStationToReponse()))
            .flatMap(Stream::distinct)
            .collect(Collectors.toList());
    }

}
