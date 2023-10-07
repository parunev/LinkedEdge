package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COMPANY_SPECIALTY")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_SPECIALTY_ID"))
public class CompanySpecialty extends BaseEntity {

    @Column(name = "SPECIALTY_NAME")
    private String specialtyName;

    @ManyToOne
    @JoinColumn(name = "COMPANY_RESOLUTION_ID")
    private CompanyResolution companyResolution;
}
