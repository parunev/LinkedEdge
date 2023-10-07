package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COMPANY_INDUSTRY")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_INDUSTRY_ID"))
public class CompanyIndustry extends BaseEntity {

    @Column(name = "INDUSTRY_NAME")
    private String industryName;

    @ManyToOne
    @JoinColumn(name = "COMPANY_RESOLUTION_ID")
    private CompanyResolution companyResolution;
}
