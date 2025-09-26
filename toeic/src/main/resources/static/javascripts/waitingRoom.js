// waitingRoom.js
(() => {
  // ===== 유틸 =====
  function getParam(name) {
    const v = new URLSearchParams(location.search).get(name);
    return v && v.trim();
  }
  function textOrDefault(id, def) {
    const el = document.getElementById(id);
    const v = el && (el.textContent || el.innerText);
    return (v && v.trim()) || def;
  }
  function updateMemberCount(n) {
    const el = document.getElementById('memberCount');
    if (el) el.textContent = Number.isFinite(n) ? String(n) : '0';
  }
  function appendLog(msg) {
    const box = document.getElementById('log');
    if (!box) return;
    const p = document.createElement('p');
    p.textContent = msg;
    box.appendChild(p);
    box.scrollTop = box.scrollHeight;
  }

  // ===== 방/사용자 =====
  const roomId = getParam('roomId') || textOrDefault('roomLabel', 'ROOM1');
  const user   = textOrDefault('userLabel', 'guest');

  // ===== STOMP =====
  let stomp = null;
  let connected = false;

  // 중복 방지 가드
  let joinedOnce = false;     // JOIN은 딱 1번만
  let roomSub = null;         // 구독도 1번만

  function connect() {
    const sock = new SockJS('/ws');
    stomp = Stomp.over(sock);
    stomp.debug = null;
    stomp.connect({}, onConnected, onError);
  }

  function onConnected() {
    connected = true;
    console.log('[WS] connected waitingRoom:', { roomId, user });

    // 구독: 이미 되어있지 않을 때만
    if (!roomSub) {
      roomSub = stomp.subscribe('/topic/room/' + roomId, onMessage);
    }

    // 입장 알림: 한 번만 보냄
    if (!joinedOnce) {
      sendJoin();
      joinedOnce = true;
    }
  }

  function onError(err) {
    connected = false;
    console.error('[WS] error:', err);
    setTimeout(connect, 2000);
  }

  function onMessage(frame) {
    const body = JSON.parse(frame.body || '{}');
    switch (body.type) {
      case 'join':
        appendLog(`${body.user} 님 입장 (${body.count})`);
        updateMemberCount(body.count);
        break;
      case 'leave':
        appendLog(`${body.user} 님 퇴장 (${body.count})`);
        updateMemberCount(body.count);
        break;
      case 'start':
        appendLog(`게임 시작! 현재 인원 ${body.count}`);
        location.href = `/gameroom?roomId=${encodeURIComponent(roomId)}`;
        break;
    }
  }

  function sendJoin() {
    if (!stomp || !connected) return;
    const payload = { roomId, user };
    console.log('[WS] send join', payload);
    stomp.send('/app/room/join', {}, JSON.stringify(payload));
  }

  function sendLeave() {
    if (!stomp) return;
    try {
      const payload = { roomId, user };
      console.log('[WS] send leave', payload);
      stomp.send('/app/room/leave', {}, JSON.stringify(payload));
    } catch (_) {}
  }

  function startGame() {
    if (!stomp || !connected) return;
    const payload = { roomId };
    console.log('[WS] start', payload);
    stomp.send('/app/room/start', {}, JSON.stringify(payload));
  }

  window.addEventListener('beforeunload', sendLeave);
  window.startGame = startGame;

  console.log('[INIT] waitingRoom', { roomId, user });
  connect();
})();
