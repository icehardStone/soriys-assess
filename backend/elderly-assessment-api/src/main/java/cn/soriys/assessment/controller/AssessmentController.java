package cn.soriys.assessment.controller;

import cn.soriys.assessment.dto.AssessmentRequest;
import cn.soriys.assessment.entity.AssessmentRecord;
import cn.soriys.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
    private final AssessmentService service;
    public AssessmentController(AssessmentService service) { this.service = service; }

    @PostMapping
    public AssessmentRecord create(@Valid @RequestBody AssessmentRequest r) {
        return service.create(r, currentUserId());
    }

    @PutMapping("/{id}")
    public AssessmentRecord update(@PathVariable Long id, @Valid @RequestBody AssessmentRequest r) {
        return service.update(id, r, currentUserId());
    }

    @GetMapping("/{id}")
    public AssessmentRequest get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public Object page(@RequestParam(defaultValue="1") long current,
                       @RequestParam(defaultValue="10") long size,
                       @RequestParam(required=false) String keyword) {
        return service.page(current, size, keyword);
    }

    @DeleteMapping("/{id}")
    public Map<String,Object> delete(@PathVariable Long id) {
        service.delete(id); return Map.of("message","删除成功");
    }

    private Long currentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
