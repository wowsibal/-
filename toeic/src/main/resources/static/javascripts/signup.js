// /static/javascripts/signup.js
document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("signup-form") || document.querySelector(".signup-form");
  if (!form) {
    console.error("signup-form not found");
    return;
  }

  const signupButton = document.getElementById("signupButton");
  const checkUserIdButton = document.getElementById("checkUserIdButton"); // 없으면 무시됨
  const nameField = document.getElementById("name");
  const useridField = document.getElementById("userid");
  const passwordField = document.getElementById("password");
  // HTML에 id가 confirmPassword 인지 confirm-password 인지 혼재할 수 있어 둘 다 시도
  const confirmPasswordField =
    document.getElementById("confirmPassword") ||
    document.getElementById("confirm-password");
  const dobField = document.getElementById("dob");

  let isUserIdValid = false; // 중복확인 결과

  function validateForm() {
    const ok =
      !!useridField?.value &&
      !!nameField?.value &&
      !!dobField?.value &&
      !!passwordField?.value &&
      !!confirmPasswordField?.value &&
      passwordField.value === confirmPasswordField.value &&
      (checkUserIdButton ? isUserIdValid : true); // 중복확인 버튼이 있으면 통과해야 함

    if (signupButton) signupButton.disabled = !ok;
    return ok;
  }

  // 아이디 중복확인 버튼이 있을 때만 이벤트 바인딩
  if (checkUserIdButton && useridField) {
    checkUserIdButton.addEventListener("click", async () => {
      const userid = useridField.value.trim();
      if (!userid) {
        alert("아이디를 입력하세요.");
        return;
      }
      try {
        const res = await fetch(`/api/users/check-duplicate-id?userid=${encodeURIComponent(userid)}`);
        if (!res.ok) throw new Error("서버 오류");
        const data = await res.json();
        if (data.exists) {
          isUserIdValid = false;
          alert("이미 존재하는 아이디입니다.");
        } else {
          isUserIdValid = true;
          alert("사용 가능한 아이디입니다.");
        }
      } catch (e) {
        console.error(e);
        alert("아이디 확인 중 오류가 발생했습니다.");
        isUserIdValid = false;
      } finally {
        validateForm();
      }
    });

    // 아이디를 수정하면 다시 확인 요구
    useridField.addEventListener("input", () => {
      isUserIdValid = false;
      validateForm();
    });
  }

  // 입력 검증 연결
  [nameField, useridField, passwordField, confirmPasswordField, dobField]
    .filter(Boolean)
    .forEach(el => el.addEventListener("input", validateForm));

  // 폼 제출
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      alert("입력값을 확인해주세요.");
      return;
    }

    const payload = {
      userid: useridField.value.trim(),
      name: nameField.value.trim(),
      password: passwordField.value,
      dob: dobField.value, // yyyy-MM-dd 형태
    };

    try {
      const res = await fetch("/api/users/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        let msg = "회원가입 실패";
        try {
          const data = await res.json();
          if (data && data.message) msg = data.message;
        } catch {
          const text = await res.text();
          if (text) msg = text;
        }
        alert(msg);
        return;
      }

      alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
      window.location.href = "/login";
    } catch (err) {
      console.error("회원가입 오류:", err);
      alert("서버 통신 중 오류가 발생했습니다.");
    }
  });

  // 초기 버튼 상태
  validateForm();
});
