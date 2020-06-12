package me.study.studyolle.settings.form;


import lombok.Data;
import me.study.studyolle.domain.Zone;

@Data
public class ZoneForm {

    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() {
        return Zone.builder()
                .city(getCityName())
                .localNameOfCity(getLocalNameOfCity())
                .province(getProvinceName())
                .build();
    }
}
