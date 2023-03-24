package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping ("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";

    private LabelService labelService;

    @Operation (summary = "Get label by ID")
    @GetMapping (ID)
    public Label getLabel(@PathVariable long id) throws NoSuchElementException {
        return labelService.findById(id);
    }

    @Operation(summary = "Get list of labels")
    @ApiResponses (@ApiResponse (responseCode = "200", content =
        @Content (schema =
        @Schema (implementation = Label.class))
        ))
    @GetMapping("")
    public List<Label> getAllLabels() throws Exception {
        return labelService.findAll();
    }

    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "Label created")
    @PostMapping("")
    @ResponseStatus (CREATED)
    public Label createLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.create(labelDto);
    }

    @Operation(summary = "Update label")
    @PutMapping (ID)
    public Label updateLabel(@PathVariable long id, @RequestBody @Valid LabelDto labelDto) {
        return labelService.update(id, labelDto);
    }

    @Operation(summary = "Delete label")
    @DeleteMapping (ID)
    public void deleteLabel(@PathVariable long id) {
        labelService.deleteById(id);
    }
}
