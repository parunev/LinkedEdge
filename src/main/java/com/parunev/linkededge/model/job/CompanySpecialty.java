package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `CompanySpecialty` class represents a specialty associated with a company in the LinkedEdge application. It extends the `BaseEntity` class
 * and includes a field for specifying the name of the specialty. This class is associated with a company resolution.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COMPANY_SPECIALTY")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_SPECIALTY_ID"))
public class CompanySpecialty extends BaseEntity {

    /**
     * The name of the specialty.
     */
    @Column(name = "SPECIALTY_NAME")
    private String specialtyName;

    /**
     * The company resolution associated with this specialty.
     */
    @ManyToOne
    @JoinColumn(name = "COMPANY_RESOLUTION_ID")
    private CompanyResolution companyResolution;
}
