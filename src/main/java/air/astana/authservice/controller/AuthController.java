package air.astana.authservice.controller;

import air.astana.authservice.model.dto.AuthRequestDto;
import air.astana.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "User registration with role USER")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid AuthRequestDto authDto) {
        authService.registration(authDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Sign in by username and password")
    @PostMapping("/login")
    public ResponseEntity<?> loginByUsername(@RequestBody @Valid AuthRequestDto authDto) {
        return ResponseEntity.ok(authService.loginByUsername(authDto));
    }

}
