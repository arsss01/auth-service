package air.astana.authservice.controller;

import air.astana.authservice.model.dto.request.AuthDto;
import air.astana.authservice.service.AuthService;
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

    @PostMapping("/signup")
    public ResponseEntity<?> registerByUsername(@RequestBody AuthDto authDto) {
        authService.registration(authDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginByUsername(@RequestBody AuthDto authDto) {
        return ResponseEntity.ok(authService.loginByUsername(authDto));
    }

}