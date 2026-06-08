package com.ubicom.Ubicom;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig에 등록된 암호화 빈

    @Override
    public void run(String... args) throws Exception {
        Integer adminId = 20233244; // 생성할 관리자 학번

        // 1. DB에 이미 해당 관리자 계정이 있는지 검사
        if (memberRepository.findByUserId(adminId).isEmpty()) {

            // 2. 관리자 객체 생성 (기존 Member 엔티티 규격에 맞게 세팅)
            Member admin = new Member();
            admin.setUserId(adminId);
            admin.setName("관리자");
            admin.setMajor("정보보안학과");

            // [중요] 스프링 시큐리티 로그인 조회를 위해 비밀번호를 반드시 암호화해서 저장합니다.
            admin.setPassword(passwordEncoder.encode("admin"));

            // 3. 만약 Member 엔티티에 권한(Role) 필드가 있다면 세팅합니다.
            // 예: admin.setRole("ROLE_ADMIN");

            memberRepository.save(admin);
            System.out.println("=== [UbiCOM] 관리자 계정이 DB에 자동으로 생성되었습니다. (ID: 20233244) ===");
        }
    }
}