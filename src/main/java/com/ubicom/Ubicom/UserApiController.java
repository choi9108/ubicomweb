package com.ubicom.Ubicom;

import jakarta.servlet.http.HttpServletResponse; // 1. import 추가
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final MemberRepository memberRepository;

    @GetMapping("/api/user")
    public Map<String, Object> getCurrentUser(
            @AuthenticationPrincipal User principal,
            jakarta.servlet.http.HttpServletResponse response) { // 1. 매개변수에 response 추가

        // 2. 서버 단에서도 브라우저 캐시를 완전히 차단하는 HTTP 헤더 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        Map<String, Object> responseData = new HashMap<>(); // 변수명 혼선 방지

        if (principal != null) {
            Integer userId = Integer.parseInt(principal.getUsername());
            var memberOpt = memberRepository.findByUserId(userId);

            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                responseData.put("isLoggedIn", true);
                responseData.put("studentId", member.getUserId());
                responseData.put("username", member.getName());
                responseData.put("department", member.getMajor());

                if (member.getUserId() == 20233244) {
                    responseData.put("isAdmin", true);
                } else {
                    responseData.put("isAdmin", false);
                }

            } else {
                responseData.put("isLoggedIn", false);
            }
        } else {
            responseData.put("isLoggedIn", false);
        }

        return responseData;
    }
}