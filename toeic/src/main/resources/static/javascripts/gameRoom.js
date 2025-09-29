// gameRoom.js
(() => {
  // ---- 유틸 ----
  function getParam(name) {
    const url = new URL(window.location.href);
    return url.searchParams.get(name);
  }
  function text(id, fallback = '') {
    const el = document.getElementById(id);
    return (el && el.textContent && el.textContent.trim()) || fallback;
  }
  function appendLog(msg) {
    const box = document.getElementById('log');
    if (!box) return;
    const p = document.createElement('p');
    p.textContent = msg;
    box.appendChild(p);
    box.scrollTop = box.scrollHeight;
  }
  function updateMemberCount(n) {
    const el = document.getElementById('memberCount');
    if (el) el.textContent = n ?? 0;
  }

  // ---- 초기 값 ----
  const roomId =
    getParam('roomId') || text('roomLabel', 'ROOM1');
  const user =
    text('userLabel', 'guest');

  // ---- STOMP 연결 ----
  let stomp;

  function connect() {
    const sock = new SockJS('/ws');
    stomp = Stomp.over(sock);
    stomp.debug = null;
    stomp.connect({}, onConnected, onError);
  }

  function onConnected() {
    console.log('[WS] connected gameRoom:', { roomId, user });
    // 방 공통 이벤트 구독(입퇴장/시작 등)
    stomp.subscribe('/topic/room/' + roomId, onRoomEvent);

    // (필요 시) 게임 전용 채널
    // stomp.subscribe('/topic/game/' + roomId, onGameEvent);

    // 입장 알림
    sendJoin();
  }

  function onError(err) {
    console.error('[WS] error:', err);
    setTimeout(connect, 2000);
  }

  function onRoomEvent(frame) {
    const body = JSON.parse(frame.body);
    if (body.type === 'join') {
      appendLog(`${body.user} 님 입장 (${body.count})`);
      updateMemberCount(body.count);
    } else if (body.type === 'leave') {
      appendLog(`${body.user} 님 퇴장 (${body.count})`);
      updateMemberCount(body.count);
    } else if (body.type === 'start') {
      appendLog(`게임 시작! 현재 인원 ${body.count}`);
      updateMemberCount(body.count);
      // TODO: 문제 로딩/게임 로직 트리거
    }
  }

  // function onGameEvent(frame) {
  //   const body = JSON.parse(frame.body);
  //   // TODO: 문제/정답/채점 등 이벤트 처리
  // }

  function sendJoin() {
    console.log('[WS] send join (game)', { roomId, user });
    stomp.send('/app/room/join', {}, JSON.stringify({ roomId, user }));
  }

  function sendLeave() {
    try {
      stomp.send('/app/room/leave', {}, JSON.stringify({ roomId, user }));
    } catch (e) {}
  }

  // 페이지 이탈 시 퇴장
  window.addEventListener('beforeunload', sendLeave);

  // 시작
  connect();
})();
