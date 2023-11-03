package br.com.projetodifm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.projetodifm.data.vo.v1.admin.PermissionForUserVO;
import br.com.projetodifm.data.vo.v1.admin.UserVO;
import br.com.projetodifm.services.AdminServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Endpoint")
public class AdminController {

    @Autowired
    private AdminServices services;
    
    @Operation(summary = "Add a Permission")
    @PostMapping(value = "/add-permission")
    public ResponseEntity<?> addPermission(@RequestBody @Valid PermissionForUserVO userPermission){
        return services.addPermission(userPermission);
    }

    @Operation(summary = "Remove a Permission")
    @PostMapping(value = "/remove-permission")
    public ResponseEntity<?> removePermission(@RequestBody @Valid PermissionForUserVO userPermission){
        return services.removePermission(userPermission);
    }

    @Operation(summary = "Finds all Users")
    @GetMapping(value = "/users")
    public ResponseEntity<PagedModel<EntityModel<UserVO>>> findAllUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction){
        return services.findAllUsers(page, size, direction);
    }

    @Operation(summary = "Finds a User")
    @GetMapping(value = "/users/{user_id}")
    public ResponseEntity<UserVO> findUserById(@PathVariable(value = "user_id") Long userId){
        return services.findUserById(userId);
    }

    @Operation(summary = "Finds all Users by Permission")
    @GetMapping(value = "/users/permission/{permission_id}")
    public ResponseEntity<PagedModel<EntityModel<UserVO>>> findAllUsersByPermissionId(
            @PathVariable(value = "permission_id") Long permissionId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction){
        return services.findAllUsersByPermissionId(permissionId, page, size, direction);
    }
}
