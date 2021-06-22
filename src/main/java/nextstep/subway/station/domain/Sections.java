package nextstep.subway.station.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        checkArgument(ObjectUtils.isNotEmpty(section), "section should not be null");
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
