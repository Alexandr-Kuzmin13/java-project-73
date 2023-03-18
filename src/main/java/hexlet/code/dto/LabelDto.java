package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelDto implements Transferable {

    private static final int MIN = 3;
    private static final int MAX = 100;

    @Size (min = MIN, max = MAX)
    @NotBlank
    private String name;
}
