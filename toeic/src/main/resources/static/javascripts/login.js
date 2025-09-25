document.querySelector(".login-form").addEventListener("submit", async (event) => {
  event.preventDefault(); // 폼 기본 동작 방지

  const userid   = document.querySelector("input[name='userid']").value.trim();
  const password = document.querySelector("input[name='password']").value;

  if (!userid || !password) {
    alert("아이디/비밀번호를 입력하세요");
    return;
  }

  try {
    const response = await fetch('/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      // ✅ username → userid 로 키 수정
      body: JSON.stringify({ userid, password })
    });

    if (!response.ok) {
      // 컨트롤러가 문자열을 보낼 수도 있으니 안전하게 처리
      let msg = '로그인 실패';
      try {
        const ct = response.headers.get('content-type') || '';
        if (ct.includes('application/json')) {
          const err = await response.json();
          msg = err.message || JSON.stringify(err);
        } else {
          msg = await response.text();
        }
      } catch (_) {}
      alert(msg || '로그인 실패');
      return;
    }

    const data = await response.json(); // { token, userid }
    localStorage.setItem('token', data.token);
    localStorage.setItem('userid', data.userid);

    window.location.href = '/start';
  } catch (e) {
    console.error('로그인 오류:', e);
    alert('서버 오류가 발생했습니다.');
  }
});
