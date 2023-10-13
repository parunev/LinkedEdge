package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * The `CompanyResolution` class represents a company's resolution or information in the LinkedEdge application. It extends the `BaseEntity` class
 * and includes various fields related to a company, such as the company's universal name, name, URL, description, staff count, and headquarters.
 * This class is associated with company specialties, industries, and a job.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COMPANY_RESOLUTION")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_RESOLUTION_ID"))
public class CompanyResolution extends BaseEntity {

    /**
     * The company's universal name.
     */
    @Column(name = "COMPANY_UNIVERSAL_NAME")
    private String companyUniversalName;

    /**
     * The company's name.
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;

    /**
     * The company's LinkedIn URL.
     */
    @Column(name = "COMPANY_URL")
    private String companyUrl;

    /**
     * The description of the company.
     */
    @Column(name = "COMPANY_DESCRIPTION", length = 30000)
    private String companyDescription;

    /**
     * The staff count of the company.
     */
    @Column(name = "COMPANY_STAFF_COUNT")
    private Integer companyStaffCount;

    /**
     * The headquarters of the company.
     */
    @Column(name = "COMPANY_HEADQUARTER")
    private String companyHeadquarter;

    /**
     * A list of specialties associated with this company.
     */
    @OneToMany(mappedBy = "companyResolution", cascade = CascadeType.ALL)
    private List<CompanySpecialty> specialties;

    /**
     * A list of industries associated with this company.
     */
    @OneToMany(mappedBy = "companyResolution", cascade = CascadeType.ALL)
    private List<CompanyIndustry> industries;

    /**
     * The job associated with this company resolution.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;
}
