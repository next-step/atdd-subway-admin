package nextstep.subway.common.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class Name {

    private final Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\\\":{}|<>]");

    @Column(unique = true, nullable = false)
    private String name;

    protected Name() {}

    public Name(String name) {
        if(isVaildName(name)) {
            this.name = name;
        }
    }

    public String printName() {
		return this.name;
	}

    public boolean isEmptyName() {
        return this.name.isEmpty();
    }

    public boolean isVaildName(String name) {
        return !specialCharPattern.matcher(name).find();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name1 = (Name) o;
        return Objects.equals(this.name, name1.name);
    }

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
