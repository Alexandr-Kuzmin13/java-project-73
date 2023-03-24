package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto implements Transferable {

    @NotBlank
    @Size (min = 3, max = 100)
    private String name;

    private String description;

    @NotNull
    private Long taskStatusId;

    private Long executorId;

    private Set<Long> labelIds;
}
