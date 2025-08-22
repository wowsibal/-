const socket = io(); // 서버 주소 명시

let currentQuestionIndex = 0; // 현재 문제 인덱스
let questions = []; // 문제 리스트
let currentUser = null; // 현재 사용자 정보

/**
 * 서버에서 문제 데이터를 가져오는 함수
 */
function getQueryParams() {
    const params = new URLSearchParams(window.location.search);
    const gameType = params.get("gameType");
    const difficulty = params.get("difficulty");
    const roomId = params.get("roomId"); // roomId 가져오기
    return { gameType, difficulty, roomId };
}

async function fetchQuestions() {
    const { gameType, difficulty } = getQueryParams();
    const collectionName = `game_${gameType}_${difficulty}`;
    try {
        const response = await fetch(`/api/quiz/questions/${encodeURIComponent(collectionName)}`);
        if (!response.ok) throw new Error("문제 데이터를 가져오는데 실패했습니다.");

        const data = await response.json();
        return data;
    } catch (error) {
        console.error("문제 데이터를 가져오는 중 오류 발생:", error);
        return [];
    }
}

/**
 * 문제와 보기를 화면에 로드하는 함수
 */
function loadQuestion() {
    const questionText = document.getElementById("question-text");
    const choicesContainer = document.getElementById("choices");

    if (questions.length === 0) {
        questionText.textContent = "권한이 없습니다.";
        return;
    }

    const question = questions[currentQuestionIndex];

    // 문제 텍스트 표시
    questionText.textContent = `Q${currentQuestionIndex + 1}. ${question.question}`;

    // 보기 표시 (기존 보기 초기화 후 추가)
    choicesContainer.innerHTML = "";
    question.choices.forEach((choice, index) => {
        const choiceItem = document.createElement("div");
        choiceItem.textContent = `${index + 1}. ${choice}`;
        choiceItem.classList.add("choice-item");
        choicesContainer.appendChild(choiceItem);
    });
}

function initializeEmptyPlayerSlots() {
    const playersContainer = document.querySelector(".players");
    const maxPlayers = 4; // 최대 플레이어 수

    playersContainer.innerHTML = ""; // 기존 슬롯 초기화
    for (let i = 0; i < maxPlayers; i++) {
        const playerSlot = document.createElement("div");
        playerSlot.className = "player";
        playerSlot.innerHTML = `
            <img src="/images/character_no.png" alt="Player ${i + 1}" class="player-image">
            <div class="player-name">Player${i + 1}</div>
        `;
        playersContainer.appendChild(playerSlot);
    }
}

socket.on("updatePlayers", (participants) => {
    console.log("수신한 참가자 데이터:", participants);

    const playersContainer = document.querySelector(".players");
    if (!playersContainer) {
        console.error("플레이어 컨테이너를 찾을 수 없습니다.");
        return;
    }

    // 기존 슬롯 초기화
    playersContainer.innerHTML = "";

    // 최대 플레이어 수
    const maxPlayers = 4;

    // 참가자 정보를 슬롯에 추가
    for (let i = 0; i < maxPlayers; i++) {
        const playerSlot = document.createElement("div");
        playerSlot.className = "player";

        if (participants[i]) {
            const player = participants[i];
            playerSlot.innerHTML = `
                <img src="/images/character_${player.character}.png" alt="${player.userId}" class="player-image">
                <div class="player-name">${player.userId}</div>
            `;
        } else {
            // 빈 슬롯
            playerSlot.innerHTML = `
                <img src="/images/character_no.png" alt="Empty Slot" class="player-image">
                <div class="player-name">Player${i + 1}</div>
            `;
        }

        playersContainer.appendChild(playerSlot);
    }
});

document.querySelector(".input-box").addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        const message = e.target.value.trim();
        const { roomId } = getQueryParams();
        if (message) {
            const question = questions[currentQuestionIndex];

            // 채팅 메시지 서버로 전송
            socket.emit("chatMessage", { roomId, message });

            // 정답 제출
            socket.emit("check answer", {
                answer: message,
                correctAnswer: question.answer,
                questionIndex: currentQuestionIndex,
                userId: currentUser?.userId || "알 수 없는 사용자",
                roomId,
            });

            e.target.value = ""; // 입력 필드 초기화

            // 마지막 문제 체크
            if (currentQuestionIndex >= questions.length - 1) {
                // 서버에 퀴즈 종료 요청
                socket.emit("endQuiz", { roomId });
            }
        }
    }
});

socket.on("answer result", ({ isCorrect, userId }) => {
    if (isCorrect) {
        alert(`${userId}님이 정답을 맞혔습니다!`);
        currentQuestionIndex++;
        loadQuestion();
    }
});

socket.on("chatMessage", (data) => {
    const chatBox = document.querySelector("#chat-messages");
    if (!chatBox) {
        console.error("chatBox 요소를 찾을 수 없습니다. HTML에 #chat-messages 요소를 추가하세요.");
        return;
    }

    const messageDiv = document.createElement("div");
    messageDiv.classList.add("chat-message");
    messageDiv.textContent = `${data.user}: ${data.message}`;
    chatBox.appendChild(messageDiv);
    chatBox.scrollTop = chatBox.scrollHeight; // 채팅창 스크롤을 최신 메시지로 이동
});

document.addEventListener("DOMContentLoaded", () => {
    const { roomId } = getQueryParams(); // 현재 방 ID 가져오기
    const storedUserId = localStorage.getItem("userid") || "Guest";
    const character = localStorage.getItem("character") || "A";

    // 방에 참가 요청
    socket.emit("joinRoom", { roomId, userId: storedUserId, character });

    // 서버로부터 사용자 상태 수신
    socket.on("userStatus", (status) => {
        const chatBox = document.querySelector("#chat-messages");
        const statusMessage = document.createElement("div");
        statusMessage.classList.add("status-message");
        statusMessage.textContent = status;
        chatBox.appendChild(statusMessage);
    });
});
document.getElementById("leave-room-btn").addEventListener("click", () => {
    const { roomId } = getQueryParams();
    const userId = localStorage.getItem("userid");

    if (roomId && userId) {
        socket.emit("leaveRoom", { roomId, userId });
        window.location.href = "/waitingroom.html";
    } else {
        alert("방 정보를 확인할 수 없습니다.");
    }
});

socket.on("updateRanking", (ranking) => {
    const rankingList = document.querySelector(".ranking-list");

    if (!rankingList) {
        console.error("ranking-list 요소를 찾을 수 없습니다.");
        return;
    }

    rankingList.innerHTML = ""; // 기존 순위 초기화

    ranking.forEach((user, index) => {
        const rankItem = document.createElement("li");
        rankItem.textContent = `${index + 1}. ${user.userId} - ${user.score}점`;
        rankingList.appendChild(rankItem);
    });

    console.log("순위가 업데이트되었습니다:", ranking);
});
socket.on("user info", (userInfo) => {
    currentUser = userInfo;
    console.log("현재 사용자 정보:", currentUser);
});

socket.on("quizEnd", () => {
    const { roomId } = getQueryParams();
    const userId = localStorage.getItem("userid");

    if (roomId && userId) {
        window.location.href = `/ranking.html?roomId=${roomId}&userId=${userId}`;
    } else {
        console.error("roomId 또는 userId를 찾을 수 없습니다. 기본 ranking.html로 이동합니다.");
        window.location.href = "/ranking.html";
    }
});

document.addEventListener("DOMContentLoaded", async () => {
    const startQuizButton = document.getElementById("start-quiz-btn");

    // 방 정보 및 방장 여부 확인
    const storedUserId = localStorage.getItem("userid") || "Guest";
    const { roomId } = getQueryParams();

    // 서버에 사용자 등록 요청
    socket.emit("register", storedUserId);

    // 서버로부터 방 정보 요청
    socket.emit("requestRoomInfo", { roomId });

    // 방 정보 수신 후 방장 여부에 따라 게임 시작 버튼 표시
    socket.on("roomInfo", ({ hostId }) => {
        if (storedUserId !== hostId) {
            // 방장이 아니라면 버튼 숨기기
            startQuizButton.style.display = "none";
        }
    });

    // 게임 시작 버튼 클릭 시 동작
    startQuizButton.addEventListener("click", async () => {
        try {
            // 서버에 게임 시작 이벤트 전송
            socket.emit("startGame", { roomId });

            // 문제 데이터를 가져와 로드
            const questions = await fetchQuestions();
            loadQuestion(questions[0], 0);

            // 방장에게만 버튼 숨기기
            startQuizButton.style.display = "none";
        } catch (error) {
            console.error("문제를 가져오는데 실패했습니다:", error);
        }
    });
    
    const character = localStorage.getItem("character") || "A";
    socket.emit("register", storedUserId);

    socket.emit("joinRoom", {
        roomId: getQueryParams().roomId,
        userId: storedUserId,
        character,
    });

    socket.on("gameStarted", () => {
        console.log("게임이 시작되었습니다!");
        // fetchQuestions로 문제를 가져온 후 로드
        fetchQuestions().then((fetchedQuestions) => {
            if (fetchedQuestions.length > 0) {
                questions = fetchedQuestions;
                loadQuestion(questions[0], 0); // 첫 번째 문제 로드
            } else {
                console.error("문제를 가져오지 못했습니다.");
            }
        });
    });

    socket.emit("request user info");
    socket.emit("requestRanking");
    initializeEmptyPlayerSlots(); 
});
