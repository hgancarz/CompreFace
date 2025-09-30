package com.exadel.frs.core.trainservice.controller;

import static com.exadel.frs.core.trainservice.system.global.Constants.API_KEY_DESC;
import static com.exadel.frs.core.trainservice.system.global.Constants.API_V1;
import static com.exadel.frs.core.trainservice.system.global.Constants.SUBJECT_DESC;
import static com.exadel.frs.core.trainservice.system.global.Constants.SUBJECT_NAME_IS_EMPTY;
import static com.exadel.frs.core.trainservice.system.global.Constants.X_FRS_API_KEY_HEADER;
import static org.springframework.http.HttpStatus.CREATED;
import com.exadel.frs.core.trainservice.dto.SubjectDto;
import com.exadel.frs.core.trainservice.service.SubjectService;
import io.swagger.annotations.ApiParam;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(API_V1 + "/recognition/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @ResponseStatus(CREATED)
    public SubjectDto createSubject(
            @ApiParam(value = API_KEY_DESC, required = true)
            @RequestHeader(X_FRS_API_KEY_HEADER)
            final String apiKey,
            @Valid
            @RequestBody
            final SubjectDto subjectDto
    ) {
        var subject = subjectService.createSubject(apiKey, subjectDto.getSubjectName());
        return new SubjectDto((subject.getSubjectName()));
    }

    @GetMapping
    public Object listSubjects(
            @ApiParam(value = API_KEY_DESC, required = true)
            @RequestHeader(X_FRS_API_KEY_HEADER)
            final String apiKey,
            @ApiParam(value = "Page number (0-based)", example = "0")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(value = "Page size (1-1000)", example = "50")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(value = "Search filter (case-insensitive prefix match)", example = "ali")
            @RequestParam(value = "search", required = false) String search,
            @ApiParam(value = "Sort field", allowableValues = "name", example = "name")
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @ApiParam(value = "Sort order", allowableValues = "asc,desc", example = "asc")
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order
    ) {
        // Backward compatibility: if none of page/size/search are provided, return legacy array
        if (page == null && size == null && search == null) {
            return Map.of("subjects", subjectService.getSubjectsNames(apiKey));
        }

        // Validate parameters
        if (page != null && page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }
        if (size != null && (size < 1 || size > 1000)) {
            throw new IllegalArgumentException("Size must be between 1 and 1000");
        }
        if (!"name".equals(sort)) {
            throw new IllegalArgumentException("Sort must be 'name'");
        }
        if (!"asc".equals(order) && !"desc".equals(order)) {
            throw new IllegalArgumentException("Order must be 'asc' or 'desc'");
        }

        // Set default values
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 50;
        
        // Create pageable with sorting
        org.springframework.data.domain.Sort.Direction direction = "desc".equals(order) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageNumber, 
                pageSize, 
                org.springframework.data.domain.Sort.by(direction, "subjectName")
        );

        // Get paged results
        org.springframework.data.domain.Page<String> subjectPage = subjectService.getSubjectsNames(apiKey, search, pageable);

        // Build response
        return com.exadel.frs.core.trainservice.dto.SubjectsResponseDto.builder()
                .items(subjectPage.getContent())
                .page(subjectPage.getNumber())
                .size(subjectPage.getSize())
                .totalElements(subjectPage.getTotalElements())
                .totalPages(subjectPage.getTotalPages())
                .sort(sort)
                .order(order)
                .build();
    }

    @PutMapping("/{subject}")
    public Map<String, Object> renameSubject(
            @ApiParam(value = API_KEY_DESC, required = true)
            @RequestHeader(X_FRS_API_KEY_HEADER)
            final String apiKey,
            @ApiParam(value = SUBJECT_DESC, required = true)
            @Valid
            @NotBlank(message = SUBJECT_NAME_IS_EMPTY)
            @PathVariable("subject")
            final String oldSubjectName,
            @Valid
            @RequestBody
            final SubjectDto subjectDto) {
        return Map.of(
                "updated",
                subjectService.updateSubjectName(apiKey, oldSubjectName, subjectDto.getSubjectName())
        );
    }

    @DeleteMapping("/{subject}")
    public Map<String, Object> deleteSubject(
            @ApiParam(value = API_KEY_DESC, required = true)
            @RequestHeader(X_FRS_API_KEY_HEADER)
            final String apiKey,
            @ApiParam(value = SUBJECT_DESC, required = true)
            @Valid
            @NotBlank(message = SUBJECT_NAME_IS_EMPTY)
            @PathVariable("subject")
            final String subjectName
    ) {
        return Map.of(
                "subject",
                subjectService.deleteSubjectByName(apiKey, subjectName)
                              .getRight()
                              .getSubjectName()
        );
    }

    @DeleteMapping
    public Map<String, Object> deleteSubjects(
            @ApiParam(value = API_KEY_DESC, required = true)
            @RequestHeader(X_FRS_API_KEY_HEADER)
            final String apiKey
    ) {
        return Map.of(
                "deleted",
                subjectService.deleteSubjectsByApiKey(apiKey)
        );
    }
}
