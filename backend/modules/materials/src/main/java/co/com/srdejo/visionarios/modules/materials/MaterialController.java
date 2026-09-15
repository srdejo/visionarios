package co.com.srdejo.visionarios.modules.materials;

import co.com.srdejo.visionarios.modules.materials.dto.MaterialResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ApiResponse<List<MaterialResponse>> findAll() {
        return ApiResponse.ok(materialService.findForCurrentUser());
    }
}
