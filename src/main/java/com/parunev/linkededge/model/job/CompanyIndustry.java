package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `CompanyIndustry` class represents an industry associated with a company in the LinkedEdge application. It extends the `BaseEntity` class
 * and includes a field for specifying the name of the industry. This class is associated with a company resolution.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COMPANY_INDUSTRY")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_INDUSTRY_ID"))
public class CompanyIndustry extends BaseEntity {

    /**
     * The name of the industry.
     */
    @Column(name = "INDUSTRY_NAME")
    private String industryName;

    /**
     * The company resolution associated with this industry.
     */
    @ManyToOne
    @JoinColumn(name = "COMPANY_RESOLUTION_ID")
    private CompanyResolution companyResolution;
}
