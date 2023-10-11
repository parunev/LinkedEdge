package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `JobFunction` class represents a job function in the LinkedEdge application. It extends the `BaseEntity` class and is typically
 * used in the "Prepare me for job" functionality. This class includes a field for specifying the name of the job function.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_JOB_FUNCTION")
@AttributeOverride(name = "id", column = @Column(name = "JOB_FUNCTION_ID"))
public class JobFunction extends BaseEntity {

    /**
     * The name of the job function.
     */
    @Column(name = "JOB_FUNCTION_NAME")
    private String jobFunctionName;

    /**
     * The job associated with this job function.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;
}
