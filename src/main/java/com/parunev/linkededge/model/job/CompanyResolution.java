package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COMPANY_RESOLUTION")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_RESOLUTION_ID"))
public class CompanyResolution extends BaseEntity {

    @Column(name = "COMPANY_UNIVERSAL_NAME")
    private String companyUniversalName;

    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "COMPANY_URL")
    private String companyUrl;

    @Column(name = "COMPANY_DESCRIPTION", length = 30000)
    private String companyDescription;

    @Column(name = "COMPANY_STAFF_COUNT")
    private Integer companyStaffCount;

    @Column(name = "COMPANY_HEADQUARTER")
    private String companyHeadquarter;

    @OneToMany(mappedBy = "companyResolution", cascade = CascadeType.ALL)
    private List<CompanySpecialty> specialties;

    @OneToMany(mappedBy = "companyResolution", cascade = CascadeType.ALL)
    private List<CompanyIndustry> industries;

    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;
}
