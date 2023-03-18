package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto implements Transferable {

    private static final int MIN = 3;
    private static final int MAX = 100;

    @NotBlank
    @Size (min = MIN, max = MAX)
    private String name;

    private String description;

    private Long taskStatusId;

    private Long executorId;

    private Set<Long> labelIds;
}
