package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table (name = "labels")
@NoArgsConstructor
@AllArgsConstructor
public class Label {

    private static final int MIN = 3;
    private static final int MAX = 100;

    @Id
    @GeneratedValue (strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Column (unique = true)
    @Size (min = MIN, max = MAX)
    private String name;

    @CreationTimestamp
    @Temporal (TIMESTAMP)
    private Date createdAt;

}
