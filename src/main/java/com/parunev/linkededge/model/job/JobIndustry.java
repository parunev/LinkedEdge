package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `JobIndustry` class represents a job industry in the LinkedEdge application. It extends the `BaseEntity` class and is typically
 * used in the "Prepare me for job" functionality. This class includes a field for specifying the name of the job industry.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_JOB_INDUSTRY")
@AttributeOverride(name = "id", column = @Column(name = "JOB_FUNCTION_ID"))
public class JobIndustry extends BaseEntity {

    /**
     * The name of the job industry.
     */
    @Column(name = "JOB_INDUSTRY_NAME")
    private String jobIndustryName;

    /**
     * The job associated with this industry.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;
}
