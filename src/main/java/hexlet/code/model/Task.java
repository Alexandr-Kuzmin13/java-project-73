package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table (name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue (strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Size (min = 3, max = 100)
    private String name;

    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn (name = "task_status_id")
    private TaskStatus taskStatus;

    @NotNull
    @ManyToOne
    @JoinColumn (name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn (name = "executor_id")
    private User executor;

    @CreationTimestamp
    @Temporal (TIMESTAMP)
    private Date createdAt;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinColumn (name = "label_id")
    private Set<Label> labels;
}
