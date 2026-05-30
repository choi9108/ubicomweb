package com.ubicom.Ubicom;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register() {
        return "forward:/register.html";
    }

    @PostMapping("/member")
    public String addMember(
            String name,
            Integer userid,
            String major,
            String password,
            HttpServletResponse response
    ) throws Exception {

        var result = usersRepository.findByUserId(userid);
        var result2 = memberRepository.findByUserId(userid);
        // 1. 학번 자릿수 검증 (8자리)
        if (userid == null || (int)(Math.log10(userid) + 1) != 8) {
            showAlert(response, "학번 8자리를 정확히 입력해주세요.");
            return null; // 스크립트를 실행하므로 뒤의 뷰 리턴은 무시됨
        }

        // 2. 존재하지 않는 학번 검증
        if (result.isEmpty()) {
            showAlert(response, "존재하지 않는 학번입니다.");
            return null;
        }

        // 3. 이미 등록된 학번 검증
        if (result2.isPresent()) {
            showAlert(response, "이미 등록된 학번입니다.");
            return null;
        }



        // 회원 가입 진행
        Member member = new Member();
        member.setName(name);
        member.setUserId(userid);
        member.setMajor(major);

        // BCryptPasswordEncoder는 빈(Bean)으로 주입받은 passwordEncoder를 사용하는 것이 좋습니다.
        var hash = passwordEncoder.encode(password);
        member.setPassword(hash);

        memberRepository.save(member);

        return "redirect:/list";
    }

    // 알림창을 띄우고 /register로 이동시키는 헬퍼 메소드
    private void showAlert(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + message + "');");
        out.println("location.href='/register';"); // 다시 회원가입 페이지로 이동
        out.println("</script>");
        out.flush();
    }
}